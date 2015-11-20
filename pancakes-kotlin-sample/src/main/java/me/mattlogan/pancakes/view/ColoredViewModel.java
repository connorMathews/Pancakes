package me.mattlogan.pancakes.view;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import auto.parcel.AutoParcel;

@AutoParcel
public abstract class ColoredViewModel implements Parcelable {
  @Nullable public abstract Integer checkedId();

  public abstract Builder toBuilder();

  public static Builder builder() {
    return new AutoParcel_ColoredViewModel.Builder();
  }

  @AutoParcel.Builder
  public static abstract class Builder {
    public abstract Builder checkedId(@Nullable Integer checkedId);
    public abstract ColoredViewModel build();
  }
}
