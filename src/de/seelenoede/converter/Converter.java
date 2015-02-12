package de.seelenoede.converter;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Converter {
	public String tmpPath;
	
	public Converter(String tmpPath)
	{
		this.tmpPath = tmpPath;
	}
	
	/**
	 * Zip images into a cbz file without compression
	 * @param filePath
	 */
	public void zip(String filePath) {
		try{
        	int BUFFER = 2048;
        	BufferedInputStream origin = null;
        	FileOutputStream dest = new FileOutputStream(filePath.substring(0, filePath.lastIndexOf('.')) + ".cbz");
        	ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest)); 
        	out.setMethod(ZipOutputStream.DEFLATED);
        	out.setLevel(0);
        	byte data[] = new byte[BUFFER];
        	
        	File f = new File(tmpPath);
        	String files[] = f.list();
        	for (int i=0; i<files.length; i++) {
        		System.out.println("Adding: "+files[i]);
        		FileInputStream fi = new FileInputStream(tmpPath + files[i]);
        		origin = new BufferedInputStream(fi, BUFFER);
        		ZipEntry entry = new ZipEntry(tmpPath + files[i]);
        		out.putNextEntry(entry);
        		int count;
            	while((count = origin.read(data, 0, BUFFER)) != -1) {
            	   out.write(data, 0, count);
            	}
            	origin.close();
        	}
        	out.close();
        	System.out.println("Lösche tmpOrdner");
        	FileUtils.deleteDirectory(f);
        }
        catch(IOException e){
        	e.printStackTrace();
        }
	}
	
	/**
	 * convert PDF to multiple images
	 * @param filePath
	 */
	public void convertToImage(String filePath)
	{
		PDDocument document = null;		
		
		try
		{
			document = PDDocument.load(filePath);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		List<PDPage>pages =  document.getDocumentCatalog().getAllPages();
        Iterator<PDPage> iter =  pages.iterator(); 
        
        int pageNumber = 0;

        while (iter.hasNext()) 
        {
            PDPage page = (PDPage) iter.next();
            try {
				BufferedImage image = page.convertToImage();
				writeImage(image, pageNumber);
				pageNumber++;
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	/**
	 * write images into temporary folder
	 * @param image
	 * @param pageNumber
	 */
	private void writeImage(BufferedImage image, int pageNumber)
	{
		File file = new File(tmpPath + pageNumber + ".png");
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
