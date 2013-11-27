import java.util.*;
import java.net.*;

public class toRelativeURLVar2 {

	/**
	 * This method converts an absolute url to an url relative to a given
	 * base-url. The algorithm is somewhat chaotic, but it works (Maybe rewrite
	 * it). Be careful, the method is ".mm"-specific. Something like this should
	 * be included in the librarys, but I couldn't find it. You can create a new
	 * absolute url with "new URL(URL context, URL relative)".
	 */
	public static String toRelativeURL(URL base, URL target) {
		// Precondition: If URL is a path to folder, then it must end with '/'
		// character.
		if ((base.getProtocol().equals(target.getProtocol()))
				&& (base.getHost().equals(target.getHost()))) {

			String baseString = base.getFile();
			String targetString = target.getFile();
			String result = "";

			// remove filename from URL
			baseString = baseString.substring(0,
					baseString.lastIndexOf("/") + 1);

			// remove filename from URL
			targetString = targetString.substring(0,
					targetString.lastIndexOf("/") + 1);

			StringTokenizer baseTokens = new StringTokenizer(baseString, "/");// Maybe
																				// this
																				// causes
																				// problems
																				// under
																				// windows
			StringTokenizer targetTokens = new StringTokenizer(targetString,
					"/");// Maybe this causes problems under windows

			String nextBaseToken = "", nextTargetToken = "";

			// Algorithm

			while (baseTokens.hasMoreTokens() && targetTokens.hasMoreTokens()) {
				
				//removed this line
				//nextBaseToken = baseTokens.nextToken();
				
				
				nextTargetToken = targetTokens.nextToken();
				if (!(nextBaseToken.equals(nextTargetToken))) {
					while (true) {
						result = result.concat("../");
						if (!baseTokens.hasMoreTokens()) {
							break;
						}
						nextBaseToken = baseTokens.nextToken();
					}
					while (true) {
						result = result.concat(nextTargetToken + "/");
						if (!targetTokens.hasMoreTokens()) {
							break;
						}
						nextTargetToken = targetTokens.nextToken();
					}
					String temp = target.getFile(); // to string
					result = result.concat(temp.substring(
							temp.lastIndexOf("/") + 1, temp.length()));
					return result;
				}
			}

			while (baseTokens.hasMoreTokens()) {
				result = result.concat("../");
				baseTokens.nextToken();
			}

			while (targetTokens.hasMoreTokens()) {
				nextTargetToken = targetTokens.nextToken();
				result = result.concat(nextTargetToken + "/");
			}

			String temp = target.getFile(); // to string
			result = result.concat(temp.substring(temp.lastIndexOf("/") + 1,
					temp.length()));
			return result;
		}
		return target.toString();
	}

}
