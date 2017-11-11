package com.y.fish.base.api.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by myliang on 3/7/17.
 */
public class ImageUtil {

    public static String[] getImages(String images, String split) {
        if (images == null || images.equals("")) {
            return new String[]{};
        } else {
            return Arrays.stream(images.split(split)).map((img) -> "/api/files/300_200/" + img ).toArray(String[]::new);
        }
    }

    /**
     *
     * @param is 输入流，原图片
     * @param fileType 图片类型 jpg, png
     * @param width 要截取的宽度
     * @param height　要截取的高度
     * @return
     * @throws IOException
     */
    public static byte[] compress(InputStream is, String fileType, int width, int height) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        BufferedImage src = ImageIO.read(is);
        int picWidth = src.getWidth();   //得到图片的宽度
        int picHeight = src.getHeight();  //得到图片的高度
        float ratio = scale(picWidth, picHeight, width, height);
        int nWidth = (int)(picWidth * ratio);
        int nHeight = (int)(picHeight * ratio);
        Image image = src.getScaledInstance(nWidth, nHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(image, 0, 0, null);
        outputImage.getGraphics().dispose();

        ImageIO.write(outputImage, fileType, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static float scale(int imageWidth, int imageHeight, int bitWidth, int bitHeight) {
        // 缩放比
        float ratio = 1;
        // Log.e("imageWidth:", imageWidth + ", imageHeight:" + imageHeight + ",bitWidth: " + bitWidth + ",bitHeight" + bitHeight);
        // 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        if (imageWidth > imageHeight && imageWidth > bitWidth) {
            // 如果图片宽度比高度大,以宽度为基准
            ratio = (float)bitWidth / imageWidth;
        } else if (imageWidth < imageHeight && imageHeight > bitHeight) {
            // 如果图片高度比宽度大，以高度为基准
            ratio = (float)bitHeight / imageHeight;
        }
        return ratio;
    }

}
