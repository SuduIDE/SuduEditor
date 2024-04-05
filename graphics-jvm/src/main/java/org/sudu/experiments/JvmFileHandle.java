package org.sudu.experiments;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

public class JvmFileHandle extends JvmFsHandle implements FileHandle {

  static final Function<byte[], String> asUtf8String = b -> new String(b, StandardCharsets.UTF_8);
  static final Function<byte[], byte[]> identity = b -> b;

  public JvmFileHandle(Path path, Path root, Executor bgWorker, Executor edt) {
    super(path, root, bgWorker, edt);
  }

  protected JvmFileHandle ctor(Executor edt) {
    return new JvmFileHandle(path, root, bgWorker, edt);
  }

  @Override
  public void getSize(IntConsumer result) {
    result.accept(getFileSize());
  }

  private int getFileSize() {
    try {
      long size = Files.size(path);
      if (size > (int) size) throw new IOException("size > (int)size");
      return (int) size;
    } catch (IOException e) {
      System.err.println(e.getMessage());
      return 0;
    }
  }

  @Override
  public void readAsText(Consumer<String> consumer, Consumer<String> onError) {
    read(consumer, onError, asUtf8String, 0, -1);
  }

  @Override
  public void readAsBytes(Consumer<byte[]> consumer, Consumer<String> onError, int begin, int length) {
    read(consumer, onError, identity, begin, length);
  }

  <T> void read(
      Consumer<T> consumer,
      Consumer<String> onError,
      Function<byte[], T> transform,
      int begin, int length
  ) {
    if (isOnWorker()) {
      read0(consumer, onError, transform, begin, length);
    } else {
      bgWorker.execute(
          () -> read0(consumer, onError, transform, begin, length));
    }
  }

  <T> void read0(
      Consumer<T> consumer,
      Consumer<String> onError,
      Function<byte[], T> transform,
      int position, int length
  ) {

    try (SeekableByteChannel ch = Files.newByteChannel(path)) {
      if (position != 0)
        ch.position(position);
      if (length < 0)
        length = intSize(ch.size());
      T apply = readChannel(transform, length, ch);
      if (isOnWorker()) {
        consumer.accept(apply);
      } else {
        edt.execute(() -> consumer.accept(apply));
      }
    } catch (IOException e) {
      String message = e.getMessage();
      if (isOnWorker()) {
        onError.accept(message);
      } else {
        edt.execute(() -> onError.accept(message));
      }
    }
  }

  static <T> T readChannel(
      Function<byte[], T> transform, int length, SeekableByteChannel ch
  ) throws IOException {
    byte[] data = new byte[length];
    ch.read(ByteBuffer.wrap(data));
    return transform.apply(data);
  }

  private int intSize(long size) throws IOException {
    int iSize = (int) size;
    if (iSize != size)
      throw new IOException("file too large");
    return iSize;
  }
}
