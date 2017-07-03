/*
 * Project Scelight
 *
 * Copyright (c) 2013 Andras Belicza <iczaaa@gmail.com>
 *
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package sc2toolkit.replay.impl;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EnumSet;
import java.util.Set;
import sc2toolkit.common.Utils;
import sc2toolkit.common.exception.TkException;
import sc2toolkit.common.exception.TkResourceException;
import sc2toolkit.common.version.impl.VersionBean;
import sc2toolkit.mpq.InvalidMpqArchiveException;
import sc2toolkit.mpq.MpqContent;
import sc2toolkit.mpq.MpqParser;

/**
 * Replay parser engine.<br>
 * Able to parse StarCraft II replay files and construct {@link Replay} objects.
 *
 * @author Andras Belicza
 */
public class RepParserEngine {

  /**
   * Version of the replay parser engine.
   */
  public static final VersionBean VERSION = new VersionBean(1, 2, 1);

  /**
   * Empty replay content set.
   */
  public static final Set< RepContent> EMPTY_CONTENT_SET = EnumSet.noneOf(RepContent.class);

  /**
   * Replay content set containing only the game events.
   */
  public static final Set< RepContent> GAME_EVENTS_CONTENT_SET = EnumSet.of(RepContent.GAME_EVENTS);

  /**
   * Full replay content set.
   */
  public static final Set< RepContent> FULL_CONTENT_SET = EnumSet.of(RepContent.MESSAGE_EVENTS, RepContent.GAME_EVENTS, RepContent.TRACKER_EVENTS);

  /**
   * Parses the specified replay file and returns a {@link Replay} object.
   *
   * @param file replay file to be parsed
   * @return the constructed {@link Replay} object
   */
  public static Replay parseReplay(final Path file) throws TkResourceException {
    return parseReplay(file, FULL_CONTENT_SET);
  }

  /**
   * Parses the specified replay file and returns a {@link Replay} object.
   *
   * @param file       replay file to be parsed
   * @param contentSet content to be parsed;
   *                   {@link RepContent#DETAILS}, {@link RepContent#INIT_DATA}
   *                   and {@link RepContent#ATTRIBUTES_EVENTS} are always
   *                   parsed; extra content is to be specified here
   * @return the constructed {@link Replay} object
   *
   * @see #getRepProc(Path)
   */
  public static Replay parseReplay(final Path file, final Set< RepContent> contentSet) throws TkResourceException {
    try (final MpqParser mpqParser = new MpqParser(file)) {
      return parseReplay(mpqParser, contentSet);
    }
  }

  /**
   * Parses the specified replay file and returns a {@link Replay} object.
   *
   * @param mpqParser  MPQ parser providing the replay content
   * @param contentSet content to be parsed;
   *                   {@link RepContent#DETAILS}, {@link RepContent#INIT_DATA}
   *                   and {@link RepContent#ATTRIBUTES_EVENTS} are always
   *                   parsed; extra content is to be specified here
   *
   * @return the constructed {@link Replay} object
   *
   * @throws InvalidMpqArchiveException if error occurs reading files from the
   *                                    MPQ archive
   */
  private static Replay parseReplay(final MpqParser mpqParser, final Set< RepContent> contentSet) throws TkResourceException {
    // Read replay header, this can be read with any protocol
    Header header = new Header(Protocol.DEFAULT.decodeHeader(mpqParser.getUserData().userData));

    final Protocol p = Protocol.get(header.getBaseBuild());

    // Contents that are always parsed:
    Details details = new Details(p.decodeDetails(mpqParser.getFile(RepContent.DETAILS)));

    InitData initData = new InitData(p.decodeInitData(mpqParser.getFile(RepContent.INIT_DATA)));

    AttributesEvents attributesEvents = new AttributesEvents(p.decodeAttributesEvents(mpqParser.getFile(RepContent.ATTRIBUTES_EVENTS)));

    int[] playerIdUserIdMap = Replay.createPlayerIdUserIdMap(initData, attributesEvents);

    // Optionally parsed contents:
    MessageEvents messageEvents;
    if (contentSet.contains(RepContent.MESSAGE_EVENTS)) {
      messageEvents = new MessageEvents(p.decodeMessageEvents(mpqParser.getFile(RepContent.MESSAGE_EVENTS), playerIdUserIdMap));
    } else {
      messageEvents = null;
    }

    GameEvents gameEvents;
    if (contentSet.contains(RepContent.GAME_EVENTS)) {
      gameEvents = new GameEvents(p.decodeGameEvents(mpqParser.getFile(RepContent.GAME_EVENTS), header, Replay.createBalanceData(header), playerIdUserIdMap));
    } else {
      gameEvents = null;
    }

    TrackerEvents trackerEvents;
    if (contentSet.contains(RepContent.TRACKER_EVENTS)) {
      final byte[] trackerData = mpqParser.getFile(RepContent.TRACKER_EVENTS);
      if (trackerData == null) {
        throw new TkResourceException("Could not retrieve tracker events file.");
      }
      trackerEvents = new TrackerEvents(p.decodeTrackerEvents(trackerData, header, playerIdUserIdMap));
    } else {
      trackerEvents = null;
    }

    return new Replay(header, details, initData, attributesEvents, messageEvents, gameEvents, trackerEvents, playerIdUserIdMap);
  }

  /**
   * Returns a {@link RepProcessor} constructed and initialized from the
   * {@link RepProcCache} for the specified replay file if it is cached, and if
   * not, it will be parsed and cached first, then returned.
   *
   * @param file replay file whose {@link RepProcessor} object to return
   * @return a {@link RepProcessor} preferably constructed and initialized from
   *         the {@link RepProcCache}
   *
   * @see #parseReplay(Path)
   */
  public static RepProcessor getRepProc(final Path file) throws TkException {
    try (final MpqParser mpqParser = new MpqParser(file)) {

      // Replay Key specification:
      // Base64 encoded string of the SHA-1 digest of the "(attributes)" content of the MPQ replay files.
      // Reasoning:
      // In case of SC2Replay files "(attributes)" contains CRC32 and MD5 hashes of all other MPQ components
      // ensuring "(attributes)" is different for all individual replays.
      // It is also very small (<300 bytes), it is stored uncompressed in SC2Replay files (mostly)
      // therefore allows fast reading and digest calculation.
      final byte[] mpqAttributes = mpqParser.getFile(MpqContent.ATTRIBUTES);
      if (mpqAttributes == null) {
        throw new TkResourceException("MPQ archive does not contain \"" + MpqContent.ATTRIBUTES.fileName + "\"!");
      }

      // Optimization: 160-bit SHA-1 is 20 bytes, in Base64 it's 20*4/3 = 26.6 chars,
      // so 27 chars is enough, Base64 generates 28 chars => cut off the last char which is always '='
      final MessageDigest md;
      try {
        md = MessageDigest.getInstance("SHA-1");
      } catch (NoSuchAlgorithmException e) {
        throw new TkException(e);
      }

      final String replayKey = Utils.toBase64String(md.digest(mpqAttributes)).substring(0, 27);

      final Replay replay = parseReplay(mpqParser, FULL_CONTENT_SET);
      if (replay == null) {
        return null;
      }
      return new RepProcessor(file, replay);
    }
  }

}
