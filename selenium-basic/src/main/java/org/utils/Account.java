package org.utils;

import java.util.Arrays;
import java.util.Optional;
import lombok.Data;

@Data
public class Account {
  private String username;
  private String password;
  private boolean isNeedAuthenticator;

  public Account(String[] args) {
    System.out.println("Account args = " + Arrays.toString(args));
    this.username = args[0];
    this.password = args[1];
    if (args.length == 3) {
      this.isNeedAuthenticator =
          Optional.ofNullable(args[2]).map(Boolean::parseBoolean).orElse(false);
    }

  }
}
