package com.work.mywork.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class ContentsUtil {


    private static int CONTACT_CODE = 0x14;

    public static void openContent(Fragment fragment) {

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        fragment.startActivityForResult(intent, CONTACT_CODE);
    }

    public static String[] onActivityResult(Context context, int requestCode, int resultCode, @Nullable Intent data) {
        String[] contacts = new String[0];
        if (requestCode == CONTACT_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return null;
            }
            Uri uri = data.getData();
            contacts = getPhoneContacts(uri, context);
        }
        return contacts;
    }

    private static String[] getPhoneContacts(Uri uri, Context context) {
        String[] contact = new String[2];
        //得到ContentResolver对象**
        ContentResolver cr = context.getContentResolver();
        //取得电话本中开始一项的光标**
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            //取得联系人姓名**
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0] = cursor.getString(nameFieldColumnIndex);
            //取得电话号码**
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            if (phone != null) {
                phone.moveToFirst();
                contact[1] = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            phone.close();
            cursor.close();
        } else {
            return null;
        }
        return contact;
    }

}
