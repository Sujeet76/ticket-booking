package utils;

import org.mindrot.jbcrypt.BCrypt;

public class UserServiceUtil {

  public static String hashPassword(String plainPsw) {
    return BCrypt.hashpw(plainPsw,BCrypt.gensalt());
  }
  public static boolean checkPassword(String givenPsw, String hashedPsw){
      return BCrypt.checkpw(givenPsw,hashedPsw);
  };

}
