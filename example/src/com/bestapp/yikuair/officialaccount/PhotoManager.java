package com.bestapp.yikuair.officialaccount;




import android.graphics.Bitmap;

import com.bestapp.yikuair.utils.AccountInfomation;

public interface PhotoManager {

	public void uploadingPhoto(Bitmap bitmap);

	public void loadingMyPhotoUrl();


	public void uploadingLikeFriend(AccountInfomation friendInfo);

	public void uploadingLocation();
}
