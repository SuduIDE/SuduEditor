package org.sudu.experiments.editor;

import org.sudu.experiments.editor.worker.proxy.FileProxy;

import java.util.Locale;

public interface Languages {

  String TEXT = "text";
  String JAVA = "java";
  String CPP = "cpp";
  String JS = "js";
  String ACTIVITY = "activity";

  static String getLanguage(String lang) {
    return switch (lang.toLowerCase(Locale.ENGLISH)) {
      case "text", "txt", "plaintext" -> TEXT;
      case "java" -> JAVA;
      case "cpp", "c++" -> CPP;
      case "js", "javascript" -> JS;
      case "activity" -> ACTIVITY;
      default -> null;
    };
  }

  static String languageFromFilename(String path) {
    if (path == null) return null;
    if (path.endsWith(".cpp")
        || path.endsWith(".cc")
        || path.endsWith(".cxx")
        || path.endsWith(".hpp")
        || path.endsWith(".c")
        || path.endsWith(".h")) return CPP;
    if (path.endsWith(".java")) return JAVA;
    if (path.endsWith(".js")) return JS;
    if (path.endsWith(".activity")) return ACTIVITY;
    return TEXT;
  }

  static String getLanguage(int type) {
    return switch (type) {
      case 0 -> TEXT;
      case 1 -> JAVA;
      case 2 -> CPP;
      case 3 -> JS;
      case 4 -> ACTIVITY;
      default -> null;
    };
  }

  static int getType(String lang) {
    return switch (lang) {
      case Languages.TEXT -> FileProxy.TEXT_FILE;
      case Languages.JAVA -> FileProxy.JAVA_FILE;
      case Languages.CPP -> FileProxy.CPP_FILE;
      case Languages.JS -> FileProxy.JS_FILE;
      case Languages.ACTIVITY -> FileProxy.ACTIVITY_FILE;
      default -> throw new IllegalArgumentException("Illegal language: " + lang);
    };
  }

}
