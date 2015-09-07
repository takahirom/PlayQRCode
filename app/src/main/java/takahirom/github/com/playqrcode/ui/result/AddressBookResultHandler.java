/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package takahirom.github.com.playqrcode.ui.result;

import android.app.Activity;
import android.telephony.PhoneNumberUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;

import com.google.android.gms.vision.barcode.Barcode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import takahirom.github.com.playqrcode.R;

/**
 * Handles address book entries.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class AddressBookResultHandler extends ResultHandler {

  private static final DateFormat[] DATE_FORMATS = {
    new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH),
    new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH),
    new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH),
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH),
  };
  static {
    for (DateFormat format : DATE_FORMATS) {
      format.setLenient(false);
    }
  }

  private static final int[] BUTTON_TEXTS = {
    R.string.button_add_contact,
    R.string.button_show_map,
    R.string.button_dial,
    R.string.button_email,
  };

  private final boolean[] fields;
  private int buttonCount;

  // This takes all the work out of figuring out which buttons/actions should be in which
  // positions, based on which fields are present in this barcode.
  private int mapIndexToAction(int index) {
    if (index < buttonCount) {
      int count = -1;
      for (int x = 0; x < MAX_BUTTON_COUNT; x++) {
        if (fields[x]) {
          count++;
        }
        if (count == index) {
          return x;
        }
      }
    }
    return -1;
  }

  public AddressBookResultHandler(Activity activity, Barcode result) {
    super(activity, result);
    Barcode.Address[] addresses = result.contactInfo.addresses;
    boolean hasAddress = addresses != null && addresses.length > 0 && addresses[0] != null && !TextUtils.isEmpty(addresses[0].addressLines[0]);
    Barcode.Phone[] phoneNumbers = result.contactInfo.phones;
    boolean hasPhoneNumber = phoneNumbers != null && phoneNumbers.length > 0;
    Barcode.Email[] emails = result.contactInfo.emails;
    boolean hasEmailAddress = emails != null && emails.length > 0;

    fields = new boolean[MAX_BUTTON_COUNT];
    fields[0] = true; // Add contact is always available
    fields[1] = hasAddress;
    fields[2] = hasPhoneNumber;
    fields[3] = hasEmailAddress;

    buttonCount = 0;
    for (int x = 0; x < MAX_BUTTON_COUNT; x++) {
      if (fields[x]) {
        buttonCount++;
      }
    }
  }

  @Override
  public int getButtonCount() {
    return buttonCount;
  }

  @Override
  public int getButtonText(int index) {
    return BUTTON_TEXTS[mapIndexToAction(index)];
  }

  @Override
  public void handleButtonPress(int index) {
    Barcode addressResult = getResult();
    Barcode.Address[] addresses = addressResult.contactInfo.addresses;
    String address1 = addresses == null || addresses.length < 1 ? null : addresses[0].addressLines[0];
    Barcode.Address address = addressResult.contactInfo.addresses[0];
    int action = mapIndexToAction(index);
    switch (action) {
      case 0:
        addContact(addressResult.contactInfo.name,
                   addressResult.contactInfo.name.pronunciation,
                   addressResult.contactInfo.phones,
                   addressResult.contactInfo.emails,
                   null,
                   null,
                   address,
                   addressResult.contactInfo.organization,
                   addressResult.contactInfo.title,
                   addressResult.contactInfo.urls,
                   null,
                   addressResult.contactInfo.addresses);
        break;
      case 1:
        searchMap(address1);
        break;
      case 2:
        dialPhone(addressResult.contactInfo.phones[0].number);
        break;
      case 3:
        sendEmail(new String[]{addressResult.contactInfo.emails[0].address}, null, null, null, null);
        break;
      default:
        break;
    }
  }

  private static Date parseDate(String s) {
    for (DateFormat currentFormat : DATE_FORMATS) {
      try {
        return currentFormat.parse(s);
      } catch (ParseException e) {
        // continue
      }
    }
    return null;
  }

  // Overriden so we can hyphenate phone numbers, format birthdays, and bold the name.
  @Override
  public CharSequence getDisplayContents() {
    Barcode result = getResult();
    StringBuilder contents = new StringBuilder(100);
    // TODO: display contents
    contents.append("not ready for display contents");
//    Barcode.maybeAppend(result.getNames(), contents);
//    int namesLength = contents.length();
//
//    String pronunciation = result.getPronunciation();
//    if (pronunciation != null && !pronunciation.isEmpty()) {
//      contents.append("\n(");
//      contents.append(pronunciation);
//      contents.append(')');
//    }
//
//    Barcode.maybeAppend(result.getTitle(), contents);
//    Barcode.maybeAppend(result.getOrg(), contents);
//    Barcode.maybeAppend(result.getAddresses(), contents);
//    String[] numbers = result.getPhoneNumbers();
//    if (numbers != null) {
//      for (String number : numbers) {
//        if (number != null) {
//          Barcode.maybeAppend(PhoneNumberUtils.formatNumber(number), contents);
//        }
//      }
//    }
//    Barcode.maybeAppend(result.getEmails(), contents);
//    Barcode.maybeAppend(result.getURLs(), contents);
//
//    String birthday = result.getBirthday();
//    if (birthday != null && !birthday.isEmpty()) {
//      Date date = parseDate(birthday);
//      if (date != null) {
//        Barcode.maybeAppend(DateFormat.getDateInstance(DateFormat.MEDIUM).format(date.getTime()), contents);
//      }
//    }
//    Barcode.maybeAppend(result.getNote(), contents);

//    if (namesLength > 0) {
//      // Bold the full name to make it stand out a bit.
//      Spannable styled = new SpannableString(contents.toString());
//      styled.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, namesLength, 0);
//      return styled;
//    } else {
      return contents.toString();
//    }
  }

  @Override
  public int getDisplayTitle() {
    return R.string.result_address_book;
  }
}
