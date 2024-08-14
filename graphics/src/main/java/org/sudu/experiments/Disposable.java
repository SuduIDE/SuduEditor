package org.sudu.experiments;

public interface Disposable {
  void dispose();

  static Disposable empty() {
    return Const.empty;
  }

  static Disposable composite(Disposable... rs) {
    return () -> {
      for (Disposable r : rs) r.dispose();
    };
  }

  static <T extends Disposable> T dispose(T d) {
    if (d != null)
      d.dispose();
    return null;
  }

  static <T extends Disposable> T assign(T oldValue, T newValue) {
    if (oldValue != null) oldValue.dispose();
    return newValue;
  }

  interface Const {
    Disposable empty = () -> {};
  }
}
