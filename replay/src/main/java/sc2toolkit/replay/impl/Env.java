/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sc2toolkit.replay.impl;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 *
 */
public class Env {

  public static final Charset UTF8 = StandardCharsets.UTF_8;
  public static final Language LANG = new Language();
  public static final Logger LOGGER = Logger.getLogger(Env.class.getName());
}
