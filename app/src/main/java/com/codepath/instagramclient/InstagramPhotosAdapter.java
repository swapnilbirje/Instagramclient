package com.codepath.instagramclient;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by sbirje on 2/7/16.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    public InstagramPhotosAdapter(Context context,  List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    //use the template to
    //display each photo

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //how to take a piece of data and turn it into view
        //data is a piece of photo
        InstagramPhoto photo = getItem(position);
        //check if we are using a recycle view...if not we need to inflate
        if(convertView==null){
            //create a new view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo,parent,false);
        }
        //look up the views for populate in the data
        TextView tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
        ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
        ImageView ivUserPhoto = (ImageView) convertView.findViewById(R.id.ivProfilePic);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvCreatedTime = (TextView) convertView.findViewById(R.id.tvCreatedTime);

        //Time spans - formatted like "42 minutes ago"
        String[] createdTimeArray = DateUtils.getRelativeTimeSpanString(photo.createdTime * 1000,
                System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_RELATIVE).toString().split(" ");

        tvCreatedTime.setText(createdTimeArray[0]+" "+createdTimeArray[1].charAt(0));
        //insert the model data into each of the view items
        tvCaption.setText(photo.caption);
        tvUserName.setText(photo.username);
        //clear the last image before inserting new image from URL
        ivPhoto.setImageResource(0);
        ivUserPhoto.setImageResource(0);

        //Make transformation for Picasso
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.GRAY)
                .borderWidthDp(1)
                .cornerRadiusDp(22)
                .scaleType(ImageView.ScaleType.FIT_XY)
                .oval(false)
                .build();

        Transformation transformationThumbnail = new RoundedTransformationBuilder()
                .borderColor(Color.GRAY)
                .scaleType(ImageView.ScaleType.FIT_START)
                .borderWidthDp(1)
                .cornerRadiusDp(10)
                .scaleType(ImageView.ScaleType.FIT_XY)
                .oval(true)
                .build();

        //insert new image - to use picasso to get image from url
        Picasso.with(getContext())
                .load(photo.imageUrl)
                .transform(transformation)
                .error(android.R.drawable.stat_notify_error)
                .placeholder(R.drawable.progress_animation)
                .into(ivPhoto);

        Picasso.with(getContext())
                .load(photo.imageUrl)
                .transform(transformationThumbnail)
                .error(android.R.drawable.stat_notify_error)
                .placeholder(R.drawable.progress_animation)
                .into(ivUserPhoto);

        return convertView;
    }

}
