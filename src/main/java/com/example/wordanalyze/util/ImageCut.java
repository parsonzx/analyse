package com.example.wordanalyze.util;




import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageCut {
    private String srcUrl;
    private String md5;
    private Integer height;
    private Integer width;
    private Integer cutHeight;
    private Integer cutWidth;
    private Integer showHeight;
    private Integer showWidth;
    private OutputStream cutOutputStream ;
    private OutputStream showOutputStream ;
    InputStream inputStream ;
    private BufferedImage src ;
    private final static Integer LIMIT_SIZE=1000*1000;

    public ImageCut(){

    }
    public void downImage(String imageUrl, String path){
        URL url = null;
        try {
            url = new URL(imageUrl);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
             BufferedImage sourceImg= ImageIO.read(dataInputStream);
            dataInputStream.close();
            Integer width=sourceImg.getWidth();
            Integer height=sourceImg.getHeight();
            if(width*height>LIMIT_SIZE){
                int scale=width*height/LIMIT_SIZE;
                Thumbnails.of(sourceImg)
                                .scale(1f/scale)
                                .outputQuality(1f)
                                .toFile(path);

                return ;
            }
            else{
                Thumbnails.of(sourceImg)
                        .scale(1f)
                        .outputQuality(1f)
                        .toFile(path);

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public ImageCut(InputStream inputStream, String srcUrl, Integer height, Integer width){
        this.inputStream =inputStream;
        this.srcUrl = srcUrl;
        this.height = height;
        this.width = width;
        this.md5 = DigestUtils.md5DigestAsHex((srcUrl + height + width).getBytes());
        this.init();
    }
    public ImageCut(File file, Integer height, Integer width) throws IOException {
        this.height = height;
        this.width = width;
        this.md5 = DigestUtils.md5DigestAsHex((file.getName() + height + width).getBytes());
        this.inputStream =new FileInputStream(file);
        this.init();
    }

    public void init() {
        try {
            Thumbnails.Builder fileBuilder = Thumbnails.of(inputStream).imageType(BufferedImage.TYPE_INT_ARGB).scale(1f).outputQuality(1f);
            src = fileBuilder.asBufferedImage();
            this.calculateSize(src.getHeight(),src.getWidth(),height,width);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] cutImage(MultipartFile  mf)throws IOException{
        InputStream inputStream=mf.getInputStream();
        BufferedImage sourceImg=ImageIO.read(inputStream);
        inputStream.close();
        byte[] b;
        width=sourceImg.getWidth();
        height=sourceImg.getHeight();
        if(width*height>LIMIT_SIZE){
            int scale=width*height/LIMIT_SIZE;
            BufferedImage bi= Thumbnails.of(sourceImg).scale(1f/scale).outputQuality(1f).asBufferedImage();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bi,"jpg",out);
            return out.toByteArray();


        }
        else{

            BufferedImage bi= Thumbnails.of(sourceImg).scale(1f).outputQuality(1f).asBufferedImage();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            boolean flag = ImageIO.write(bi ,".jpg",out);
             b = out.toByteArray();
            out.close();

        }
        return b;
    }

    public BufferedImage getShowBuff() {
        BufferedImage bf = null;
        try {
            bf = Thumbnails.of(src).imageType(BufferedImage.TYPE_INT_ARGB).sourceRegion(Positions.CENTER, showWidth, showHeight).width(showWidth).height(showHeight).asBufferedImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  bf;
    }
    public BufferedImage getShowBuff(Integer width, Integer height) {
        BufferedImage bf = null;
        try {
            bf = Thumbnails.of(src).imageType(BufferedImage.TYPE_INT_ARGB).sourceRegion(Positions.CENTER, showWidth, showHeight).width(width).height(height).asBufferedImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  bf;
    }

    public BufferedImage getCutBeforeShowBuff() {
        BufferedImage bf = null;
        try {
            bf = Thumbnails.of(src).imageType(BufferedImage.TYPE_INT_ARGB).sourceRegion(Positions.CENTER, cutWidth, cutHeight).width(cutWidth).height(cutHeight).asBufferedImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  bf;
    }
    public void toBeforeScaleOutputStream(OutputStream os) {
        try {
            Thumbnails.of(src).imageType(BufferedImage.TYPE_INT_ARGB).outputFormat("png").sourceRegion(Positions.CENTER, cutWidth, cutHeight).width(cutWidth).height(cutHeight).toOutputStream(os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void toCutOutputStream(OutputStream os) {
        try {
            Thumbnails.of(src).imageType(BufferedImage.TYPE_INT_ARGB).sourceRegion(Positions.CENTER, cutWidth, cutHeight).width(width).height(height).outputFormat("png").toOutputStream(os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public BufferedImage getCutBufferdImage() {
        try {
            return Thumbnails.of(src).imageType(BufferedImage.TYPE_INT_ARGB).sourceRegion(Positions.CENTER, cutWidth, cutHeight).width(width).height(height).outputFormat("png").asBufferedImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private void calculateSize(int srcHeight, int srcWidth, int cutHeight, int cutWidth) {
        this.calculateCutSize(srcHeight,srcWidth,cutHeight,cutWidth);
        this.calculateShowSize();
    }
    private void calculateCutSize(int srcHeight, int srcWidth, int cutHeight, int cutWidth) {
        float heightRate = (float) srcHeight / cutHeight;
        float widthRate = (float) srcWidth / cutWidth;
        if (heightRate > widthRate) {
            Float heightFloat = cutHeight * widthRate;
            this.cutHeight = heightFloat.intValue();
            this.cutWidth = srcWidth;
        } else {
            Float widthFloat = cutWidth * heightRate;
            this.cutHeight = srcHeight;
            this.cutWidth = widthFloat.intValue();
        }
    }
    private void calculateShowSize() {
        if(this.cutWidth > this.cutHeight){
            this.showHeight = this.cutWidth;
            this.showWidth = this.cutWidth;
        }else{
            this.showWidth = this.cutHeight;
            this.showHeight = this.cutHeight;
        }
    }
    public String urlToLocal(String urlStr, String fileName, String savePath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        File file = new File(saveDir+ File.separator+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }


        return file.getPath();
    }
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public void unsetBufferImage(){
        src = null;
    }


    public OutputStream getCutOutputStream() {
        return cutOutputStream;
    }

    public void setCutOutputStream(OutputStream cutOutputStream) {
        this.cutOutputStream = cutOutputStream;
    }

    public OutputStream getShowOutputStream() {
        return showOutputStream;
    }

    public void setShowOutputStream(OutputStream showOutputStream) {
        this.showOutputStream = showOutputStream;
    }



    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSrcUrl() {
        return srcUrl;
    }

    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String downImage(String imageUrl) {
        URL url = null;
        BufferedImage bi=null;
        String image="";
        try {
            url = new URL(imageUrl);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            BufferedImage sourceImg= ImageIO.read(dataInputStream);
            dataInputStream.close();
            Integer width=sourceImg.getWidth();
            Integer height=sourceImg.getHeight();

            if(width*height>LIMIT_SIZE){
                int scale=width*height/LIMIT_SIZE;
                bi=Thumbnails.of(sourceImg)
                        .scale(1f/scale)
                        .outputQuality(1f).asBufferedImage();



            }
            else{
                 bi=Thumbnails.of(sourceImg)
                        .scale(1f)
                        .outputQuality(1f).asBufferedImage();


            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bi,"jpg",out);
            byte[] imageByte=out.toByteArray();
            BASE64Encoder encoder=new BASE64Encoder();
            image=encoder.encode(imageByte);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
