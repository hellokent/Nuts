package com.nuts.lib.log;

import android.text.TextUtils;

import java.io.IOException;

import com.nuts.lib.BuildConfig;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by 陈阳(chenyang@edaijia-staff.cn>)
 * Date: 6/5/14 11:41 AM.
 */
class LogConfigItem {
    LogLevel mLevel = BuildConfig.DEBUG ? LogLevel.VERBOSE : LogLevel.WARN;
    String mTag;
    String mPackagePrefix;
    String mFile;
    boolean mEnable = true;
    boolean mUpload = false;
    boolean mUi = false;

    public LogConfigItem(XmlPullParser parser, final String defaultPackage) throws IOException, XmlPullParserException {
        for (int i = 0, n = parser.getAttributeCount(); i < n; ++i) {
            final String name = parser.getAttributeName(i);
            final String value = parser.getAttributeValue(i);
            if ("range".equalsIgnoreCase(name)) {
                mPackagePrefix = value;

                //处理默认包名
                if (!TextUtils.isEmpty(defaultPackage) && !TextUtils.isEmpty(mPackagePrefix)) {
                    if (".".equals(mPackagePrefix)) {
                        mPackagePrefix = defaultPackage;
                    } else if (mPackagePrefix.startsWith(".")) {
                        mPackagePrefix = defaultPackage + mPackagePrefix;
                    }
                }
            } else if ("min".equalsIgnoreCase(name)) {
                mLevel = LogLevel.valueOf(value.toUpperCase());
            } else if ("enable".equalsIgnoreCase(name)) {
                mEnable = Boolean.parseBoolean(value);
            } else if ("file".equalsIgnoreCase(name)) {
                mFile = value;
            } else if ("upload".equalsIgnoreCase(name)) {
                mUpload = Boolean.parseBoolean(value.toLowerCase());
            } else if ("ui".equalsIgnoreCase(name)) {
                mUi = Boolean.parseBoolean(value.toLowerCase());
            }
        }
        mTag = parser.nextText().trim();
    }

    public boolean checkClassName(final String className) {
        return className.startsWith(mPackagePrefix);
    }

    public LogLevel getLevel() {
        return mLevel;
    }

    public boolean isEnable() {
        return mEnable;
    }

    public String getTag() {
        return mTag;
    }

    public String getFile() {
        return mFile;
    }

    public String getPackage() {
        return mPackagePrefix;
    }

    public boolean isUpload() {
        return mUpload;
    }

    @Override
    public String toString() {
        return "LogConfigItem{" +
                "tag='" + mTag + '\'' +
                ", packagePrefix='" + mPackagePrefix + '\'' +
                ", level=" + mLevel +
                ", enable=" + mEnable +
                ", filePath='" + mFile + '\'' +
                '}';
    }
}
