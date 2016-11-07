package io.demor.nuts.sample.lib.module;

public class SimpleObject {
    public String mName;
    public int mAge;
    public boolean mGender;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SimpleObject object = (SimpleObject) o;

        if (mAge != object.mAge) return false;
        if (mGender != object.mGender) return false;
        return mName != null ? mName.equals(object.mName) : object.mName == null;

    }

    @Override
    public int hashCode() {
        int result = mName != null ? mName.hashCode() : 0;
        result = 31 * result + mAge;
        result = 31 * result + (mGender ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SimpleObject{" +
                "mName='" + mName + '\'' +
                ", mAge=" + mAge +
                ", mGender=" + mGender +
                '}';
    }
}
