import java.net.URL;


public class Tools {
	
	/**
     * This is a correction of a method getFile of a class URL.  Namely, on Windows it
     * returned file paths like /C: etc., which are not valid on Windows. This correction
     * is heuristic to a great extend. One of the reasons is that file:// is basically no
     * protocol at all, but rather something every browser and every system uses slightly
     * differently.
     */
    public static String urlGetFile(URL url) {
       String osNameStart = "Win";
       String fileSeparator = System.getProperty("file.separator");
       if (osNameStart.equals("Win") && url.getProtocol().equals("file")) {          
          String fileName = url.toString().replaceFirst("^file:","").replace('/','\\');
          return (fileName.indexOf(':') >= 0) ?
             fileName.replaceFirst("^\\\\*","") :
             fileName; } // Network path
       else {
          return url.getFile(); }}
	
}


