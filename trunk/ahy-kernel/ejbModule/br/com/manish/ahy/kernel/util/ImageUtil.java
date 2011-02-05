//  Ahy - A pure java CMS.
//  Copyright (C) 2010 Sidney Leal (manish.com.br)
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package br.com.manish.ahy.kernel.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class ImageUtil {
    private static Log log = LogFactory.getLog(ImageUtil.class);

    private ImageUtil() {
        super();
    }
    
    /*
    public static BufferedImage resize(BufferedImage image, int width, int height) {
        int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    public static BufferedImage blurImage(BufferedImage image) {
        float ninth = 1.0f/9.0f;
        float[] blurKernel = {
            ninth, ninth, ninth,
            ninth, ninth, ninth,
            ninth, ninth, ninth
        };

        Map map = new HashMap();
        
        map.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RenderingHints hints = new RenderingHints(map);
        BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, hints);
        return op.filter(image, null);
    }
    
    private static BufferedImage createCompatibleImage(BufferedImage image) {
        GraphicsConfiguration gc = BufferedImageGraphicsConfig.getConfig(image);
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage result = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        Graphics2D g2 = result.createGraphics();
        g2.drawRenderedImage(image, null);
        g2.dispose();
        return result;
    }
    
    public static BufferedImage resizeBetter(BufferedImage image, int width, int height) {
        image = createCompatibleImage(image);
        //image = resize(image, 100, 100);
        //image = blurImage(image);
        image = resize(image, width, height);
        return image;
    }
*/
    
    public static ByteArrayOutputStream scale(ByteArrayInputStream bais, int width, int height) throws IOException {
            BufferedImage src = ImageIO.read(bais);
            BufferedImage dest = scale(src, width, height);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(dest,"JPG", baos);
            return baos;
    }

    public static BufferedImage scale(BufferedImage src, int width, int height) throws IOException {
        BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dest.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance(
                (double)width/src.getWidth(),
                (double)height/src.getHeight());
        g.drawRenderedImage(src,at);        
        return dest;
}

    
    public static ByteArrayOutputStream crop(ByteArrayInputStream bais, int width, int height) throws IOException { 
        BufferedImage src = ImageIO.read(bais);        
        BufferedImage clipping = crop(src, width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(clipping,"JPG", baos);
        return baos;
    }  
    
    public static BufferedImage crop(BufferedImage src, int width, int height) throws IOException { 
        int x = src.getWidth()/2 - width/2;
        int y = src.getHeight()/2 - height/2;
        
//        System.out.println("---" + src.getWidth() + " - " + src.getHeight() + " - " + x + " - " + y);
        
        BufferedImage clipping = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);//src.getType());  
        Graphics2D area = (Graphics2D) clipping.getGraphics().create();  
        area.drawImage(src, 0, 0, clipping.getWidth(), clipping.getHeight(), x, y, x + clipping.getWidth(),  
            y + clipping.getHeight(), null);  
        area.dispose(); 
        
        return clipping;
    }  
    
    public static ByteArrayOutputStream smartCrop(ByteArrayInputStream bais, int width, int height) throws IOException { 
        BufferedImage src = ImageIO.read(bais);
        
        Float scale;
        if (src.getWidth() > src.getHeight()) {
            scale = Float.valueOf(height) / Float.valueOf(src.getHeight());
            if (src.getWidth() * scale < width) {
                scale = Float.valueOf(width) / Float.valueOf(src.getWidth());
            }            
        } else {
            scale = Float.valueOf(width) / Float.valueOf(src.getWidth());
            if (src.getHeight() * scale < height) {
                scale = Float.valueOf(height) / Float.valueOf(src.getHeight());
            }            
        }
//        
//        System.out.println("--- " + src.getWidth() + " - " + width);
//        System.out.println("--- " + src.getHeight() + " - " + height);
//        System.out.println("--- " + scale + " -- " + Float.valueOf(src.getWidth() * scale).intValue() + " -- " + Float.valueOf(src.getHeight() * scale).intValue());
//        
        BufferedImage temp = scale(src, Float.valueOf(src.getWidth() * scale).intValue(), 
                Float.valueOf(src.getHeight() * scale).intValue());

        temp = crop(temp, width, height);
                
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(temp,"JPG", baos);
        
        return baos;
      }      
}
