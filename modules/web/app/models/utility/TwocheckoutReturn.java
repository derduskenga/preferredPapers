package models.utility;

import java.util.HashMap;
import org.apache.commons.codec.digest.DigestUtils;
import play.*;

public class TwocheckoutReturn{
      public static boolean check(HashMap<String, String> args, String secret) {
	    boolean response;
	    String hashString = secret + args.get("sid") + args.get("order_number") + args.get("total");
	    String hash = DigestUtils.md5Hex(hashString).toUpperCase();
	    if (args.get("key").equals(hash)) {
		response = true;
	    }else {
		response = false;
	    }
	    return response;
      }
}