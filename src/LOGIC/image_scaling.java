package LOGIC;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

public class image_scaling {
	//ネットでは綺麗だと
	public static BufferedImage reSize3(BufferedImage img, int width, int height) {
		ResampleOp resampleOp = new ResampleOp(width, height);
		resampleOp.setFilter(ResampleFilters.getLanczos3Filter());
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp);
		BufferedImage rescaled = resampleOp.filter(img, null);
		return rescaled;
	}
	//ImageMagic
	public static void ImageMagic(String[] args) throws IOException, InterruptedException {
        //ImageMagickのパス
        //String imageMagickPath = "C:\\Program Files\\ImageMagick-6.8.7-Q16\\convert.exe"; //←Windowsの場合の例
        String imageMagickPath = "/usr/bin/convert";
        //読み取りたい画像ファイルの保存場所
        String inputFilePath = "input.jpg";
        //リサイズ後の画像ファイルの保存場所
        String outputFilePath = "output.jpg";
        //リサイズ後の幅(0以下の場合は高さに合わせて自動調整)
        int width = 640;
        //リサイズ後の高さ(0以下の場合は高さに合わせて自動調整)
        int height = 0;
        //リサイズ後の画像形式(jpg/png/gif)
        String type = "jpg";
        //リサイズ後の画像形式がjpgの場合のdpi(0以下の場合は変更しない)
        int dpi = 72;

        //リサイズ処理
        File source = new File(inputFilePath);
        File target = new File(outputFilePath);
        //ImageMagickのリサイズ指定文字列
        String size;
        if (height <= 0) {
            size = String.valueOf(width);
        }
        else if (width <= 0) {
            size = "x" + height;
        }
        else {
            size = width + "x" + height + "!";
        }

        String[] commandArray = new String[5 + ((dpi > 0) ? 2 : 0) + (("jpg".equals(type)) ? 2 : 0)];
        int index = 0;
        commandArray[index++] = imageMagickPath;
        if (dpi > 0) {
            commandArray[index++] = "-density";
            commandArray[index++] = String.valueOf(dpi);
        }
        //jpgの場合、元画像の透過部分を消す
        if ("jpg".equals(type)) {
            commandArray[index++] = "-alpha";
            commandArray[index++] = "deactivate";
        }
        commandArray[index++] = "-resize";
        commandArray[index++] = size;
        commandArray[index++] = source.getAbsolutePath();
        commandArray[index++] = type + ":" + target.getAbsolutePath();

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        StringBuilder logBuilder = new StringBuilder();
        StringBuilder errorBuilder = new StringBuilder();
        int status = 0;

        try {
            process = runtime.exec(commandArray);
            final InputStream in = process.getInputStream();
            final InputStream ein = process.getErrorStream();

            Runnable inputStreamThread = () -> {
                BufferedReader reader = null;
                String line;
                try {
                    reader = new BufferedReader(new InputStreamReader(in));
                    while (true) {
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        logBuilder.append(line).append("\n");
                    }
                }
                catch (IOException e) {
                }
                finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        }
                        catch (IOException e) {
                        }
                    }
                }
            };
            Runnable errorStreamThread = () -> {
                BufferedReader reader = null;
                String line;
                try {
                    reader = new BufferedReader(new InputStreamReader(ein));
                    while (true) {
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        errorBuilder.append(line).append("\n");
                    }
                }
                catch (IOException e) {
                }
                finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        }
                        catch (IOException e) {
                        }
                    }
                }
            };

            Thread inThread = new Thread(inputStreamThread);
            Thread errorThread = new Thread(errorStreamThread);

            inThread.start();
            errorThread.start();

            status = process.waitFor();
            inThread.join();
            errorThread.join();
        }
        finally {
            if (process != null) {
                try {
                    process.destroy();
                }
                catch (Exception e) {
                }
            }
        }
    }


}
