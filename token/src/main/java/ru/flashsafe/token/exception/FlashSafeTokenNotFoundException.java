package ru.flashsafe.token.exception;

/**
 * 
 * 
 * @author Andrew
 *
 */
public class FlashSafeTokenNotFoundException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -6751530421469271854L;
  
  public FlashSafeTokenNotFoundException() {
      super();
  }
  
  public FlashSafeTokenNotFoundException(String tokenId) {
      super("The token with Id = " + tokenId + " was not found");
  }

}
