import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

public class Vmap {
	
	private String getProperty(String propertyString)
	{	
		String property = null;
		
		if(propertyString.equals("default_browser_command_windows_nt")){
			property = "cmd.exe /c start {0}";
		}
		if(propertyString.equals("default_browser_command_windows_9x")){
			property = "command.com /c start {0}";
		}
		if(propertyString.equals("default_browser_command_other_os")){
			property = "opera {0}";
		}
		if(propertyString.equals("default_browser_command_mac")){
			property = "open -a /Applications/Safari.app {0}";
		}
		
		
		return property;
	}
	
	/**
     * Open url in WWW browser. This method hides some differences between operating systems.
     */
    public void openDocument(URL url) throws Exception {
        // Originally, this method determined external application, with which the document
        // should be opened. Which application should open which document type was
        // configured in Vmap properties file. As a result, Vmap tried to solve the
        // problem (of determining application for a file type), which should better be
        // solved somewhere else. Indeed, on Windows, this problem is perfectly solved by
        // Explorer. On KDE, this problem is solved by Konqueror default browser. In
        // general, most WWW browsers have to solve this problem.

        // As a result, the only thing we do here, is to open URL in WWW browser.
    	
        String osName = "Windows XP ";//System.getProperty("os.name");
        if (osName.substring(0,3).equals("Win")) {
            String propertyString = new String("default_browser_command_windows");
			if (osName.indexOf("9") != -1 || osName.indexOf("Me") != -1) {
				propertyString += "_9x";
			} else {
				propertyString += "_nt";
			}
			
            String browser_command=new String();
            String command=new String();
            // Here we introduce " around the parameter of explorer
            // command. This is not because of possible spaces in this
            // parameter - it is because of "=" character, which causes
            // problems. My understanding of MSDOS is not so good, but at
            // least I can say, that "=" is used in general for the purpose
            // of variable assignment.
            //String[] call = { browser_command, "\""+url.toString()+"\"" };
            try  {
                // This is working fine on Windows 2000 and NT as well
                // Below is a piece of code showing how to run executables directly
                // without asking. However, we don't want to do that. Explorer will run
                // executable, but ask before it actually runs it.
                //
                // Imagine you download a package of maps containing also nasty
                // executable. Let's say there is a map "index.mm". This map contains a
                // link to that nasty executable, but the name of the link appearing to the
                // user does not indicate at all that clicking the link leads to execution
                // of a programm.  This executable is located on your local computer, so
                // asking before executing remote executable does not solve the
                // problem. You click the link and there you are running evil executable.

                // build string for default browser:
                // ask for property about browser: fc, 26.11.2003.
                Object[] messageArguments = { url.toString() };
                MessageFormat formatter = new MessageFormat(getProperty(propertyString));
                browser_command = formatter.format(messageArguments);
        
                if (url.getProtocol().equals("file")) {
                    command = "rundll32 url.dll,FileProtocolHandler "+Tools.urlGetFile(url);
                } else if (url.toString().startsWith("mailto:")) {
                    command = "rundll32 url.dll,FileProtocolHandler "+url.toString(); 
                } else {
                    command = browser_command; 
                }
                Runtime.getRuntime().exec(command); 
            }
            catch(IOException x) {
                //c.errorMessage("Could not invoke browser.\n\nVmap excecuted the following statement on a command line:\n\""+command+"\".\n\nYou may look at the user or default property called '"+propertyString+"'.");
                System.err.println("Caught: " + x); 
            }
        } else if (osName.startsWith("Mac OS")) {
             String urlString = url.toString();
             String browser_command=new String();
            try {
                // build string for default browser:
                if (url.getProtocol().equals("file")) {
                    urlString=urlString.replace('\\','/').replaceAll(" ","%20"); 
                }

                Object[] messageArguments = { urlString, urlString };
                // ask for property about browser: fc, 26.11.2003.
                MessageFormat formatter = new MessageFormat(getProperty("default_browser_command_mac"));
                browser_command = formatter.format(messageArguments);
                Runtime.getRuntime().exec(browser_command); 
            } catch(IOException ex2) {
                //c.errorMessage("Could not invoke browser.\n\nVmap excecuted the following statement on a command line:\n\""+browser_command+"\".\n\nYou may look at the user or default property called 'default_browser_command_mac'.");
                System.err.println("Caught: " + ex2); 
            }
        } else {
            // There is no '"' character around url.toString (compare to Windows code
            // above). Putting '"' around does not work on Linux - instead, the '"'
            // becomes part of URL, which is malformed, as a result.	 

//             String urlString = url.toString();
//             if (url.getProtocol().equals("file")) {
//                 urlString = urlString.replace('\\','/').replaceAll(" ","%20"); }
//             // ^ This is more of a heuristic than a "logical" code

            String browser_command=new String();
            try {
                // build string for default browser:
                String correctedUrl = new String(url.toExternalForm());
                // ask for property about browser: fc, 26.11.2003.
                Object[] messageArguments = { correctedUrl, url.toString() };
                MessageFormat formatter = new MessageFormat(getProperty("default_browser_command_other_os"));
                browser_command = formatter.format(messageArguments);
                Runtime.getRuntime().exec(browser_command); }
            catch(IOException ex2) {
               // c.errorMessage("Could not invoke browser.\n\nVmap excecuted the following statement on a command line:\n\""+browser_command+"\".\n\nYou may look at the user or default property called 'default_browser_command_other_os'.");
                System.err.println("Caught: " + ex2); 
            }
        }
    }
	
}

