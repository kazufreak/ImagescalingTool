package LOGIC;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

public class ResizeImage extends javax.swing.JFrame {
	  private int longerSize; //画像の長辺がこのピクセルになるように拡縮する
	  private int resizedW;
	  private int resizedH;
	
	  //MainLogic
	  public void resizing(String filepath, String expath) {
		  try{
			  BufferedImage originalImage = ImageIO.read(new File(filepath));

			  // 新規画像のサイズ計算
			  longerSize = 600;	// 長辺のサイズを600pxに縮小
			  sizeCalculate(originalImage, longerSize);

			  // 画像サイズ変更
			  BufferedImage scaleImg = scaleImage(originalImage, resizedW, resizedH); 
			  
			  // 縮小後の画像を画質を指定してファイルに出力
			  // 文{}の終わりでリソース(imageStream)が確実に閉じられるよう try-with-resources文を使う
			  try (ImageOutputStream imageStream = 
			          ImageIO.createImageOutputStream(new File(expath)
			  )) {
			    // imageStreamの画質に利用される(paramをIIOImageに渡す)
			    JPEGImageWriteParam param = new JPEGImageWriteParam(Locale.getDefault());
			    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			    param.setCompressionQuality(1f);	// 画質 0f(低) ～ 1f(高)

			    // ファイルに出力
			    ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
			    writer.setOutput(imageStream);
			    writer.write(null, new IIOImage(scaleImg, null, null), param);
			    imageStream.flush();
			    writer.dispose();
			  }
			}catch(Exception e){
			  this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR ));
			  System.out.println(e);
			}
	  }
	  
	//縮小後のサイズを計算
	  private void sizeCalculate(BufferedImage bimg, int longerSize) {
	    double percentage;
	    //長辺を基準として縦横percentageの縮小倍率を求める
	    if(bimg.getWidth() >= bimg.getHeight()){
	      percentage = ((double)longerSize / bimg.getWidth());
	      // 長辺
	      resizedW = longerSize;
	      // 短辺
	      resizedH = (int)(bimg.getHeight() * percentage);
	    }else{
	      percentage = ((double)longerSize / bimg.getHeight());
	      // 短辺
	      resizedW = (int)(bimg.getWidth() * percentage);
	      // 長辺
	      resizedH = longerSize;
	    }
	    }
	// サイズの変更
	  private BufferedImage scaleImage(Image img, int dw, int dh) {
	    // image のサイズを(dw, dh)に変更する
	    BufferedImage resizeImage =
	      new BufferedImage(dw, dh, BufferedImage.TYPE_3BYTE_BGR);
	      resizeImage.createGraphics().drawImage(
	        img.getScaledInstance(dw, dh, Image.SCALE_AREA_AVERAGING),
	           0, 0, dw, dh, null);
	    return resizeImage;
	  } 
	  
}
