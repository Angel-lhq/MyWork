//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing.camera;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.google.zxing.LuminanceSource;

public final class PlanarYUVLuminanceSource extends LuminanceSource {
    private final byte[] yuvData;
    private final int dataWidth;
    private final int dataHeight;
    private final int left;
    private final int top;

    public PlanarYUVLuminanceSource(byte[] yuvData, int dataWidth, int dataHeight, int left, int top, int width, int height) {
        super(width, height);
        if(left + width <= dataWidth && top + height <= dataHeight) {
            this.yuvData = yuvData;
            this.dataWidth = dataWidth;
            this.dataHeight = dataHeight;
            this.left = left;
            this.top = top;
        } else {
            throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
        }
    }

    public byte[] getRow(int y, byte[] row) {
        if(y >= 0 && y < this.getHeight()) {
            int width = this.getWidth();
            if(row == null || row.length < width) {
                row = new byte[width];
            }

            int offset = (y + this.top) * this.dataWidth + this.left;
            System.arraycopy(this.yuvData, offset, row, 0, width);
            return row;
        } else {
            throw new IllegalArgumentException("Requested row is outside the image: " + y);
        }
    }

    public byte[] getMatrix() {
        int width = this.getWidth();
        int height = this.getHeight();
        if(width == this.dataWidth && height == this.dataHeight) {
            return this.yuvData;
        } else {
            int area = width * height;
            byte[] matrix = new byte[area];
            int inputOffset = this.top * this.dataWidth + this.left;
            if(width == this.dataWidth) {
                System.arraycopy(this.yuvData, inputOffset, matrix, 0, area);
                return matrix;
            } else {
                byte[] yuv = this.yuvData;

                for(int y = 0; y < height; ++y) {
                    int outputOffset = y * width;
                    System.arraycopy(yuv, inputOffset, matrix, outputOffset, width);
                    inputOffset += this.dataWidth;
                }

                return matrix;
            }
        }
    }

    public boolean isCropSupported() {
        return true;
    }

    public int getDataWidth() {
        return this.dataWidth;
    }

    public int getDataHeight() {
        return this.dataHeight;
    }

    public Bitmap renderCroppedGreyscaleBitmap() {
        int width = this.getWidth();
        int height = this.getHeight();
        int[] pixels = new int[width * height];
        byte[] yuv = this.yuvData;
        int inputOffset = this.top * this.dataWidth + this.left;

        for(int y = 0; y < height; ++y) {
            int outputOffset = y * width;

            for(int x = 0; x < width; ++x) {
                int grey = yuv[inputOffset + x] & 255;
                pixels[outputOffset + x] = -16777216 | grey * 65793;
            }

            inputOffset += this.dataWidth;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
