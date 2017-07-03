/*
 * Project Scelight
 *
 * Copyright (c) 2013 Andras Belicza <iczaaa@gmail.com>
 *
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package sc2toolkit.replay.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * SC2 region (gateway).
 *
 * @author Andras Belicza
 */
public enum Region {

  /**
   * US
   */
  US("US", "US", 1, "http://usb.depot.battle.net:1119/", "http://us.battle.net/", BnetLang.ENGLISH, EnumSet.of(BnetLang.ENGLISH,
          BnetLang.SPANISH, BnetLang.PORTUGUESE), Realm.NORTH_AMERICA, Realm.LATIN_AMERICA),
  /**
   * Europe
   */
  EUROPE("Europe", "EU", 2, "http://eub.depot.battle.net:1119/", "http://eu.battle.net/", BnetLang.ENGLISH, EnumSet.of(BnetLang.ENGLISH,
          BnetLang.GERMAN, BnetLang.FRENCH, BnetLang.SPANISH, BnetLang.RUSSIAN, BnetLang.ITALIAN, BnetLang.POLISH), Realm.EUROPE, Realm.RUSSIA),
  /**
   * Korea
   */
  KOREA("Korea", "KR", 3, "http://krb.depot.battle.net:1119/", "http://kr.battle.net/", BnetLang.KOREAN, EnumSet.of(BnetLang.KOREAN,
          BnetLang.CHINESE_TRADITIONAL), Realm.KOREA, Realm.TAIWAN),
  /**
   * China
   */
  CHINA("China", "CN", 5, "http://cnb.depot.battle.net:1119/", "http://www.battlenet.com.cn/", BnetLang.CHINESE_TRADITIONAL, EnumSet
          .of(BnetLang.CHINESE_TRADITIONAL), Realm.CHINA),
  /**
   * South East Asia
   */
  SEA(
          "SEA",
          "SG",
          6,
          "http://sg.depot.battle.net:1119/",
          "http://sea.battle.net/",
          BnetLang.ENGLISH,
          EnumSet.of(BnetLang.ENGLISH),
          Realm.SEA),
  /**
   * Public Test
   */
  PUBLIC_TEST("Public Test", "XX", 98, "http://xx.depot.battle.net:1119/", "http://us.battle.net/", BnetLang.ENGLISH, EnumSet
          .of(BnetLang.ENGLISH)),
  /**
   * Unknown
   */
  UNKNOWN("Unknown", "", -1, "http://unknown.depot.battle.net:1119/", "http://unknown.battle.net/", BnetLang.ENGLISH, EnumSet
          .of(BnetLang.ENGLISH));

  /**
   * Text value of the region.
   */
  public final String text;

  /**
   * Region code.
   */
  public final String code;

  /**
   * Region id.
   */
  public final int regionId;

  /**
   * Depot server URL of the region.
   */
  public final URL depotServerUrl;

  /**
   * URL of the region's battle.net web site.
   */
  public final URL bnetUrl;

  /**
   * Default language ({@link BnetLang}) on the region's web page.
   */
  public final BnetLang defaultLang;

  /**
   * Available languages ({@link BnetLang}) on the region's web page.
   */
  public final EnumSet< BnetLang> langSet;

  /**
   * An unmodifiable set of the available languages ({@link BnetLang}) on the
   * region's web page.
   */
  public final Set< BnetLang> unmodLangSet;

  /**
   * Realms of the region.
   */
  public final Realm[] realms;

  /**
   * An unmodifiable list of the realms of the region.
   */
  public final List< Realm> realmList;

  private static URL createUrl(final String spec) throws IllegalArgumentException {
    return createUrl(null, spec);
  }

  /**
   * Creates a new {@link URL} from the specified URL context and spec.
   * <p>
   * <p>
   * Primary goal of this factory method is to suppress the
   * {@link MalformedURLException} that comes with the {@link URL}'s
   * constructor, it is thrown in a "shadowed" manner, wrapped in an
   * {@link IllegalArgumentException} (which is a {@link RuntimeException}).
   * </p>
   *
   * @param context URL context in which to interpret the URL spec
   * @param spec    URL spec to create a {@link URL} from
   * @return a new {@link URL} from the specified URL spec
   * @throws IllegalArgumentException if the specified URL spec is a malformed
   *                                  URL
   *
   * @see #createUrl(String)
   * @see URL#URL(URL, String)
   */
  private static URL createUrl(final URL context, final String spec) throws IllegalArgumentException {
    try {
      return new URL(context, spec);
    } catch (final MalformedURLException mue) {
      throw new RuntimeException("Malformed URL: " + spec, mue);
    }
  }

  /**
   * Creates a new {@link Region}.
   *
   * @param text           text value
   * @param code           region code
   * @param regionId       region id
   * @param depotServerUrl Depot server URL of the region
   * @param bnetUrl        URL of the region's battle.net web site
   * @param defaultLang    default language ({@link BnetLang}) on the region's
   *                       web page
   * @param langSet        available languages ({@link BnetLang}) on the
   *                       region's web page
   * @param realms         realms of the region
   */
  private Region(final String text, final String code, final int regionId, final String depotServerUrl, final String bnetUrl,
          final BnetLang defaultLang, final EnumSet< BnetLang> langSet, final Realm... realms) {
    this.text = text;
    this.code = code;
    this.regionId = regionId;
    this.depotServerUrl = createUrl(depotServerUrl);
    this.bnetUrl = createUrl(bnetUrl);
    this.defaultLang = defaultLang;
    this.langSet = langSet;
    unmodLangSet = Collections.unmodifiableSet(langSet);
    this.realms = realms;
    realmList = Collections.unmodifiableList(Arrays.asList(realms));
  }

  public String getCode() {
    return code;
  }

  public int getRegionId() {
    return regionId;
  }

  public URL getDepotServerUrl() {
    return depotServerUrl;
  }

  public URL getBnetUrl() {
    return bnetUrl;
  }

  public BnetLang getDefaultLang() {
    return defaultLang;
  }

  public Realm getRealm(int realmId) {
    return --realmId < realms.length ? realms[realmId] : Realm.UNKNOWN;
  }

  public Set< ? extends BnetLang> getLangSet() {
    return unmodLangSet;
  }

  public List< ? extends Realm> getRealmList() {
    return realmList;
  }

  @Override
  public String toString() {
    return text;
  }

  /**
   * Returns the region associated with the specified code.
   *
   * @param code region code to return the region for
   * @return the region associated with the specified code; or {@link #UNKNOWN}
   *         if no region is found for the specified code
   */
  public static Region fromCode(final String code) {
    for (final Region r : VALUES) {
      if (r.code.equals(code)) {
        return r;
      }
    }

    return UNKNOWN;
  }

  /**
   * Returns the region associated with the specified region id.
   *
   * @param regionId region id to return the region for
   * @return the region associated with the specified region id; or
   *         {@link #UNKNOWN} if no region is found for the specified region id
   */
  public static Region fromRegionId(final int regionId) {
    for (final Region r : VALUES) {
      if (r.regionId == regionId) {
        return r;
      }
    }

    return UNKNOWN;
  }

  /**
   * Returns an HTML table summarizing the supported languages at each region,
   * the default being underlined.
   *
   * @return an HTML table summarizing the supported languages at each region,
   *         the default being underlined
   */
  public static String getLanguageSupportTable() {
    final StringBuilder sb = new StringBuilder(512);

    sb.append("<table border=1 cellspacing=0 cellpadding=3>");
    sb.append("<tr><th>Region<th align=left>Supported Languages");
    for (final Region r : VALUES) {
      if (r == PUBLIC_TEST || r == UNKNOWN) {
        continue;
      }
      sb.append("<tr><td>").append(r.text);
      sb.append("<td><u>").append(r.defaultLang.text).append("</u>");
      for (final BnetLang bl : r.langSet) {
        if (bl != r.defaultLang) {
          sb.append(", ").append(bl.text);
        }
      }
    }
    sb.append("</table>");

    return sb.toString();
  }

  /**
   * Cache of the values array.
   */
  public static final Region[] VALUES = values();

}
