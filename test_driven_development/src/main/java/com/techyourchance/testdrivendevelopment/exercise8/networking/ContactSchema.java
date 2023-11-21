package com.techyourchance.testdrivendevelopment.exercise8.networking;

import java.util.Objects;

public class ContactSchema {
    private final String mId;
    private final String mFullName;
    private final String mFullPhoneNumber;
    private final String mImageUrl;
    private final double mAge;

    public ContactSchema(String id, String fullName, String fullPhoneNumber, String imageUrl, double age) {
        mId = id;
        mFullName = fullName;
        mFullPhoneNumber = fullPhoneNumber;
        mImageUrl = imageUrl;
        mAge = age;
    }

    public String getId() {
        return mId;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getFullPhoneNumber() {
        return mFullPhoneNumber;
    }

    public double getAge() {
        return mAge;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactSchema that = (ContactSchema) o;
        return Double.compare(that.mAge, mAge) == 0 && mId.equals(that.mId) && mFullName.equals(that.mFullName) && mFullPhoneNumber.equals(that.mFullPhoneNumber) && mImageUrl.equals(that.mImageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mFullName, mFullPhoneNumber, mImageUrl, mAge);
    }
}
