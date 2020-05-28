package program;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * @Author: Matthew Finzel (mfinzel5@gmail.com)
 * 
 * You have my permission to use this program but you may not redistribute it without my permission. 
 */
public class program {
	public static boolean roughen = false;
	public static Random rand = new Random(12);
	public static int numberModified = 0;
	public static boolean smoothingEnabled = true;
	public static int detailPreservationThreshold = 4;
	public static boolean beforeAndAfterMode = false;

	public static boolean onlyAffectEdges = false;
	public static String outputPathString;
	public static String inputPathString;

	public static void main(String[] args) {
		boolean validInputPath = false;
		boolean validOutputPath = false;

		String outputPathString = null;
		String inputPathString = null;
		System.out.println("---------------------------------------------------------");
		System.out.println("-----------------King's Texture Smoother-----------------");
		System.out.println("-----Created by: Matthew Finzel (mfinzel5@gmail.com)-----");
		System.out.println("-------------------------Version 2-----------------------");
		System.out.println("");
		if(args.length==0){
			Scanner input = new Scanner(System.in);
			while(!validInputPath){
				System.out.println("Please enter the path to the folder containing the files you would like to convert: ");
				inputPathString = input.next();
				if(inputPathString.equalsIgnoreCase("default")){
					inputPathString = "G:\\Texture_manipulations\\input";

					validInputPath = true;
				}
				else if(!inputPathString.contains(" ")){
					Path inputPath = Paths.get(inputPathString);
					if(Files.exists(inputPath)){
						validInputPath = true;
					}
					else{
						System.out.println("The path: \""+inputPathString+"\" is not a valid path.");
					}
				}
				else{
					System.out.println("Path cannot contain spaces!");
				}
			}
			while(!validOutputPath){
				System.out.println("Please enter the path to the folder you would like the converted files to be placed in: ");
				outputPathString = input.next();
				if(outputPathString.equalsIgnoreCase("default")){
					outputPathString = "G:\\Texture_manipulations\\output";

					validOutputPath = true;
				}
				else if(!outputPathString.contains(" ")){
					Path outputPath = Paths.get(outputPathString);
					if(Files.exists(outputPath)){
						validOutputPath = true;
					}
					else{
						System.out.println("The path: \""+outputPathString+"\" is not a valid path.");
					}
				}
				else{
					System.out.println("Path cannot contain spaces!");
				}
			}

			System.out.println("Would you like to enable diagonal edge blending?(Y/N) (This makes diagonal edges appear smoother)");

			boolean properInput = false;
			String response = "";
			while(!properInput){
				response = input.next();
				if(response.equalsIgnoreCase("Y")||response.equalsIgnoreCase("Yes")){
					smoothingEnabled = true;
					properInput = true;
					//before and after mode option
					System.out.println("Would you like to enable before and after mode? (results will show image before and after)");
					boolean properInput2 = false;
					String response2 = "";
					while(!properInput2){
						response2 = input.next();
						if(response2.equalsIgnoreCase("Y")||response2.equalsIgnoreCase("Yes")){
							beforeAndAfterMode = true;
							properInput2 = true;
						}
						else if(response2.equalsIgnoreCase("N")||response2.equalsIgnoreCase("No")){
							beforeAndAfterMode = false;
							properInput2 = true;
						}
						else{
							System.out.println("Please respond with either: \"Yes\",\"yes\",\"Y\",\"y\",\"No\",\"no\",\"N\", or \"n\" (without quotes).");
						}
					}
				}
				else if(response.equalsIgnoreCase("N")||response.equalsIgnoreCase("No")){
					smoothingEnabled = false;
					properInput = true;
				}
				else{
					System.out.println("Please respond with either: \"Yes\",\"yes\",\"Y\",\"y\",\"No\",\"no\",\"N\", or \"n\" (without quotes).");
				}
			}

			System.out.println("What threshold would you like for detail preservation?");
			System.out.println("A number between 0 and 8, I recomend either 4 or 5, 4 will give you a smoother look but");
			System.out.println("may shrink small details such as eyes unlike 5 which is slightly more pixelated.");

			properInput = false;

			while(!properInput){
				detailPreservationThreshold = getInt("Please enter an integer: ");
				if(detailPreservationThreshold>=0&&detailPreservationThreshold<=8){
					properInput = true;
				}
				else{
					System.out.println("Please enter a number between 0 and 8, a value of 4 or 5 is recommended.");
				}		
			}
		}
		else{
			//input path
			inputPathString = args[0];//input path
			outputPathString = args[1];//output path
			String response = args[2];//smoothing
			detailPreservationThreshold = Integer.valueOf(args[3]);//detail preservation
			if(!inputPathString.contains(" ")){
				Path inputPath = Paths.get(inputPathString);
				if(Files.exists(inputPath)){
					validInputPath = true;
				}
				else{
					System.out.println("The path: \""+inputPathString+"\" is not a valid path.");
				}
			}
			else{
				System.out.println("Path cannot contain spaces!");
			}
			if(!outputPathString.contains(" ")){
				Path outputPath = Paths.get(outputPathString);
				if(Files.exists(outputPath)){
					validOutputPath = true;
				}
				else{
					System.out.println("The path: \""+outputPathString+"\" is not a valid path.");
				}
			}
			else{
				System.out.println("Path cannot contain spaces!");
			}
			
			if(response.equalsIgnoreCase("Y")||response.equalsIgnoreCase("Yes")){
				smoothingEnabled = true;
				
			}
			else if(response.equalsIgnoreCase("N")||response.equalsIgnoreCase("No")){
				smoothingEnabled = false;
			}
			else{
				System.out.println("Please respond with either: \"Yes\",\"yes\",\"Y\",\"y\",\"No\",\"no\",\"N\", or \"n\" (without quotes).");
			}
			
			
			if(detailPreservationThreshold>=0&&detailPreservationThreshold<=8){
				
			}
			else{
				System.out.println("Please enter a number between 0 and 8, a value of 4 or 5 is recommended.");
			}
		}
		//String path = program.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		//System.out.println(path);

		//System.out.println(path);
		int totalFiles = getNumberOfFiles(inputPathString);
		if(totalFiles>0){
			numberModified=1;
		}
		int totalFixed = sharpenAllFiles(inputPathString,outputPathString, totalFiles,true);
		//int totalFixed = lowerAllImages(inputPathString,outputPathString, totalFiles);
		//int totalFixed = combineRelatedImages(inputPathString,outputPathString, totalFiles);
		//int totalFixed = addSilouette(inputPathString,outputPathString, totalFiles);

		//smoothAllFiles(path);
		//int totalFixed = shrinkAllImages(path,totalFiles);
		if(totalFiles>0)
			System.out.println("Improved a total of "+(totalFixed-1)+" textures, "+(totalFiles-(totalFixed-1))+" could not be improved.");
		else
			System.out.println("Improved a total of "+(totalFixed)+" textures, "+(totalFiles-(totalFixed))+" could not be improved.");

	}
	static int getInt(String prompt) {
		System.out.print(prompt);
		while(true){
			try {
				return Integer.parseInt(new Scanner(System.in).next());
			} catch(NumberFormatException ne) {
				System.out.print("That's not a whole number.\n"+prompt);
			}
		}
	}
	public static int combineRelatedImages(String inputPath, String outputPath, int totalFiles){
		File folder = new File(inputPath);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {		
			if (listOfFiles[i].isFile()) {
				if(!(listOfFiles[i].getName().contains("s")||listOfFiles[i].getName().contains("b"))){
					String pathToImg1 = (inputPath+"/"+listOfFiles[i].getName());
					String pathToImg2 = (inputPath+"/"+listOfFiles[i].getName()).substring(0, (inputPath+"/"+listOfFiles[i].getName()).length()-4)+"b.png";
					String pathToImg3 = (inputPath+"/"+listOfFiles[i].getName()).substring(0, (inputPath+"/"+listOfFiles[i].getName()).length()-4)+"s.png";
					String pathToImg4 = (inputPath+"/"+listOfFiles[i].getName()).substring(0, (inputPath+"/"+listOfFiles[i].getName()).length()-4)+"sb.png";
					System.out.println(pathToImg1);
					System.out.println(pathToImg2);
					System.out.println(pathToImg3);
					System.out.println(pathToImg4);
					BufferedImage img1 = Images.load(pathToImg1);
					BufferedImage img2 = Images.load(pathToImg2);
					BufferedImage img3 = Images.load(pathToImg3);
					BufferedImage img4 = Images.load(pathToImg4);
					BufferedImage result = new BufferedImage( img1.getWidth()*2,img1.getHeight()*2, BufferedImage.TYPE_INT_ARGB);
					Graphics g = result.getGraphics();
					g.drawImage(img1,0,0,null);
					g.drawImage(img2,0,160,null);
					g.drawImage(img3,160,0,null);
					g.drawImage(img4,160,160,null);

					//Images.save(result, "F:\\BattlersResult"+"/"+listOfFiles[i].getName());
					Images.save(result, outputPath+"/"+listOfFiles[i].getName());
				}
			}
			if (listOfFiles[i].isDirectory()&&!listOfFiles[i].getName().equals("Animations")) {
				System.out.println("entering directory: "+listOfFiles[i].getName());
				combineRelatedImages(inputPath+"/"+listOfFiles[i].getName(),outputPath,totalFiles);
			}
		}
		return 0;
	}
	public static int checkForColor(String inputPath, String outputPath, int totalFiles){
		File folder = new File(inputPath);
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> relevantFiles = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {		
			if (listOfFiles[i].isFile()) {
				//if(!(listOfFiles[i].getName().contains("s")||listOfFiles[i].getName().contains("b"))){
				String pathToImg1 = (inputPath+"/"+listOfFiles[i].getName());

				//System.out.println(pathToImg1);
				if((pathToImg1.contains(".png")||pathToImg1.contains(".PNG"))){
					BufferedImage img1 = Images.load(pathToImg1);
					if(img1!=null){
						//determine distance between lowest non-transparent pixel and bottom of image
						boolean found = false;
						for(int x = 0;x<img1.getWidth();x++){
							for(int y = 0;y<img1.getHeight();y++){
								Color current = new Color(img1.getRGB(x, y),true);
								if(current.getRed()==184&&current.getGreen()==248&&current.getBlue()==136){
									if(!relevantFiles.contains(listOfFiles[i].getName())){
										relevantFiles.add(listOfFiles[i].getName());
									}
								}
							}
						}
					}
				}

				//}
			}
			if (listOfFiles[i].isDirectory()&&!listOfFiles[i].getName().equals("Animations")) {
				System.out.println("entering directory: "+listOfFiles[i].getName());
				checkForColor(inputPath+"/"+listOfFiles[i].getName(),outputPath,totalFiles);
			}
		}
		System.out.println("-------------------------------------------------");
		for(int i = 0; i<relevantFiles.size();i++){
			System.out.println(relevantFiles.get(i));			
		}
		System.out.println("-------------------------------------------------");
		return 0;
	}
	public static int lowerAllImages(String inputPath, String outputPath, int totalFiles){
		File folder = new File(inputPath);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {		
			if (listOfFiles[i].isFile()) {
				//if(!(listOfFiles[i].getName().contains("s")||listOfFiles[i].getName().contains("b"))){
				String pathToImg1 = (inputPath+"/"+listOfFiles[i].getName());

				System.out.println(pathToImg1);

				BufferedImage img1 = Images.load(pathToImg1);

				BufferedImage result = new BufferedImage( img1.getWidth(),img1.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics g = result.getGraphics();

				//determine distance between lowest non-transparent pixel and bottom of image
				int lowest = -1;
				int lowestx = -1;
				for(int x = img1.getHeight()-1;x>=0;x--){
					for(int y = img1.getWidth()-1;y>=0;y--){
						if(new Color(img1.getRGB(y, x),true).getAlpha()>245){
							if(lowest==-1){
								lowest = x;
								lowestx = y;
								System.out.println(y+","+x);
							}
						}
					}
				}
				g.drawImage(img1,0,img1.getHeight()-lowest-1,null);

				//Images.save(result, "F:\\BattlersResult"+"/"+listOfFiles[i].getName());
				Images.save(result, outputPath+"/"+listOfFiles[i].getName());
				//}
			}
			if (listOfFiles[i].isDirectory()&&!listOfFiles[i].getName().equals("Animations")) {
				System.out.println("entering directory: "+listOfFiles[i].getName());
				combineRelatedImages(inputPath+"/"+listOfFiles[i].getName(),outputPath,totalFiles);
			}
		}
		return 0;
	}
	public static int addSilouette(String inputPath, String outputPath, int totalFiles){
		File folder = new File(inputPath);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {		
			if (listOfFiles[i].isFile()) {
				//if(!(listOfFiles[i].getName().contains("s")||listOfFiles[i].getName().contains("b"))){
				String pathToImg1 = (inputPath+"/"+listOfFiles[i].getName());

				System.out.println(pathToImg1);

				BufferedImage img1 = Images.load(pathToImg1);

				BufferedImage result = new BufferedImage( img1.getWidth()+160,img1.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics g = result.getGraphics();

				for(int x = 0;x<160;x++){
					for(int y = 0;y<img1.getHeight();y++){
						Color current = new Color(img1.getRGB(x, y),true);

						result.setRGB(x+img1.getWidth(), y, new Color(20,20,20,current.getAlpha()).getRGB());

					}
				}
				g.drawImage(img1,0,0,null);

				//Images.save(result, "F:\\BattlersResult"+"/"+listOfFiles[i].getName());
				Images.save(result, outputPath+"/"+listOfFiles[i].getName());
				//}
			}
			if (listOfFiles[i].isDirectory()&&!listOfFiles[i].getName().equals("Animations")) {
				System.out.println("entering directory: "+listOfFiles[i].getName());
				addSilouette(inputPath+"/"+listOfFiles[i].getName(),outputPath,totalFiles);
			}
		}
		return 0;
	}
	public static int sharpenAllFiles(String inputPath, String outputPath, int totalFiles, boolean scaledUp){

		File folder = new File(inputPath);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {		
			if (listOfFiles[i].isFile()) {
				BufferedImage img = Images.load(inputPath+"/"+listOfFiles[i].getName());
				BufferedImage original = Images.load(inputPath+"/"+listOfFiles[i].getName());
				System.out.println("Color of top left:"+new Color(img.getRGB(0, 0), true).getAlpha());
				System.out.println("Converting ("+numberModified+"/"+totalFiles+") "+(int)(((double)numberModified/(double)totalFiles)*100)+"% : "+inputPath+"/"+listOfFiles[i].getName());
				if(img == null)
					continue;
				img = Images.pixelate(img, 2);
				original = Images.pixelate(img, 2);
				
				if(scaledUp){
					Images.resizeImage(img);
				}
				if(Images.getImageScale(img)==4){//if the image is 4x scale

					if(Images.getImageScale(inputPath+"/"+listOfFiles[i].getName())==4){
						//shrink the image
						img = Images.shrink(img);
						//sharpen the 2x scale image
						img = Images.sharpen(img,original,inputPath+"/"+listOfFiles[i].getName());

						//scale up the image back to 4x scale
						img = Images.resizeImage(img);
					}

					//sharpen one more time
					if(listOfFiles[i].getName().contains(".png")||listOfFiles[i].getName().contains(".PNG")){
						
						img = Images.sharpen(img,original,inputPath+"/"+listOfFiles[i].getName());
						

						//save the changes to the image
						String resultPath = outputPath;//path.replaceAll("/textures","/results/textures");
						//System.out.println("Saving to: "+resultPath);
						//if(path.contains("Battlers")){
						//img = Images.randomizeImageColor(img);
						//}
						//img = Images.smoothImage(img);
						if(program.roughen)
							img = Images.roughenImage(img);
						System.out.println("saving to: "+resultPath);
						Images.save(img, outputPath+"/"+listOfFiles[i].getName());

						numberModified++;

					}

				}
				else{
					if(Images.getImageScale(img)!=1){
						
						BufferedImage result = Images.sharpen(img,original,inputPath+"/"+listOfFiles[i].getName());
						if(result!= null){
							boolean wasSharpened = img.equals(result);
							if(!wasSharpened){
								numberModified++;

							}
							result = Images.smoothImage(result,original, inputPath+"/"+listOfFiles[i].getName());

							Images.save(result, outputPath+"/"+listOfFiles[i].getName());
						}
					}
					else{

						//img = Images.smoothImage(img);
						//save the changes to the image
						//String resultPath = path.replaceAll("/textures","/results/textures");

						img = Images.smoothImage(img,original,inputPath+"/"+listOfFiles[i].getName());

						Images.save(img, outputPath+"/"+listOfFiles[i].getName());
					}
				}


			} 
			if (listOfFiles[i].isDirectory()&&!listOfFiles[i].getName().equals("Animations")) {
				System.out.println("entering directory: "+listOfFiles[i].getName());
				sharpenAllFiles(inputPath+"/"+listOfFiles[i].getName(),outputPath+"/"+listOfFiles[i].getName(),totalFiles,scaledUp);
			}
		}

		return numberModified;
	}
	//	public static void smoothAllFiles(String path){
	//		File folder = new File(path);
	//		File[] listOfFiles = folder.listFiles();
	//		for (int i = 0; i < listOfFiles.length; i++) {		
	//			if (listOfFiles[i].isFile()) {
	//				//smooth the image
	//				String resultPath = path.replaceAll("/textures","/results/textures");
	//
	//				System.out.println("result path: "+resultPath+"/"+listOfFiles[i].getName());
	//				BufferedImage temp = Images.load(resultPath+"/"+listOfFiles[i].getName());
	//				temp = Images.smoothImage(temp);
	//
	//				//save changes
	//				Images.save(temp, resultPath+"/"+listOfFiles[i].getName());
	//			}
	//			if (listOfFiles[i].isDirectory()) {
	//				smoothAllFiles(path+"/"+listOfFiles[i].getName());
	//			}
	//		}
	//
	//	}
	public static int randomNumber(int min, int max){

		if(max==min){
			return max;
		}
		int randNum = rand.nextInt((max-min)+1) + min;
		//System.out.println("generated random number: "+randNum+" in the range "+min+"-"+max);
		return randNum;
		//return min + (int)(Math.random() * ((max - min) + 1));
	}
	public static int getNumberOfFiles(String path){
		int count = 0;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				count++;
			} 
			if (listOfFiles[i].isDirectory()) {
				count+=getNumberOfFiles(path+"/"+listOfFiles[i].getName());
			}
		}
		return count;
	}


}
