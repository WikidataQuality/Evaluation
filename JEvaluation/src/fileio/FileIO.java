package fileio;

import java.io.*;
import java.util.ArrayList;

import static eval.Evaluation.*;


public class FileIO {
	
	public static File[] listDirectories(String path) throws FileIOException {
		path = baseDir + path;
		File filePath = new File(path);
		if (!filePath.exists()) {
			throw new FileIOException("Path '" + path + "' does not exist.");
		}
		ArrayList<File> fileList = new ArrayList<File>();
		try {
			File[] allFiles = filePath.listFiles();
			for (File file: allFiles) {
				if (file.isDirectory()) {
					fileList.add(file);
				}
			}
		} catch (Exception ex) {
			throw new FileIOException("Error listing directories in '" + path + "'.");
		}
		File[] files = new File[fileList.size()];
		files = fileList.toArray(files);
		return files;
	}
	
	public static void createDirectory(String path) throws FileIOException {
		path = baseDir + path;
		File filePath = new File(path);
		try {
			filePath.mkdirs();
		}
		catch (Exception ex) {
			throw new FileIOException("Error creating directory '" + path + "'.");
		}
	}
	
	public static void deleteDirectory(String path) throws FileIOException {
		//TODO
	}
	
	public static File[] listFiles(String path, String type) throws FileIOException {
		path = baseDir + path;
		File filePath = new File(path);
		if (!filePath.exists()) {
			throw new FileIOException("Path '" + path + "' does not exist.");
		}
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith("." + type.toLowerCase())) {
					return true;
				} else {
					return false;
				}
			}
		};
		ArrayList<File> fileList = new ArrayList<File>();
		try {
			File[] allFiles = filePath.listFiles(filter);
			for (File file: allFiles) {
				if (file.isFile()) {
					fileList.add(file);
				}
			}
		} catch (Exception ex) {
			throw new FileIOException("Error listing files in '" + path + "'.");
		}
		File[] files = new File[fileList.size()];
		files = fileList.toArray(files);
		return files;
	}
	
	public static String readFile(String path, String name, String type) throws IOException {
		path = baseDir + path;
		File filePath = new File(path);
		if (!filePath.exists()) {
			throw new FileIOException("Path '" + path + "' does not exist.");
		}
		name = path + fileSep + name + "." + type;
		File file = new File(name);
		if (!file.exists()) {
			throw new FileIOException("File '" + name + "' does not exist.");
		}
		String data = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(name), "utf-8"));
			StringBuilder builder = new StringBuilder();
	    	String line = reader.readLine();
	    	while (line != null) {
	    		builder.append(line);
	    		builder.append(lineSep);
	    		line = reader.readLine();
	    	}
	    	data = builder.toString();
		} catch (Exception ex) {
			throw new FileIOException("Error reading from file '" + name +"'.");
		} finally {
	    	reader.close();
		}
		return data;
	}
	
	public static void writeFile(String path, String name, String type, String data) throws IOException {
		path = baseDir + path;
		name = path + fileSep + name + "." + type;
		BufferedWriter writer = null;
		try {
			File filePath = new File(path);
			filePath.mkdirs();
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name), "utf-8"));
			String[] lines = data.split(lineSep);
			for (int i=0; i<lines.length-1; i++) {
				writer.write(lines[i]);
				writer.write(lineSep);
			}
			writer.write(lines[lines.length-1]);
		} catch (Exception ex) {
			throw new FileIOException("Error writing to file '" + name + "'.");
		} finally {
	    	writer.close();
		}
		return;
	}
	
	public static void deleteFile(String path, String name) {
		//TODO
	}
	
}
