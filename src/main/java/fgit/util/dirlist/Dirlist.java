package fgit.util.dirlist;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.apache.xmlbeans.XmlObject;

import dirlist.DirectoryTreeDocument;
import dirlist.LeafDocument;

public class Dirlist 
{
    /**
     *  Args 0 = absolute directory name.
     *          [N.B: "E:" for a top level device.]
     * @throws Exception 
     */
    public static void main(String args[]) throws Exception {
        // Holds the directory listing in reverse order as the dir is scanned.
        ArrayList<String> list = new ArrayList<String>(100);
        
         
       // DirectoryTreeDocument dirList = DirectoryTreeDocument.Factory.newInstance();
        DirectoryTreeDocument dirList = DirectoryTreeDocument.Factory.newInstance();
        


        BigDecimal size = new BigDecimal(0);
        File f = new File(args[0]);

        size = list(f, PADSTR, list, dirList);

        // To Do: Filters
        //          Dirs only
        //          Dirs/Files with size above given limit
        //          Dir level
        String fileEntry = "";
        for (int i=list.size()-1; i>=0; i--) {
            fileEntry = (String)list.get(i);
            if (fileEntry.trim().startsWith("Dir"))
            System.out.println(fileEntry);
        }
        System.out.println(dirList);
    }

    private static BigDecimal list(File file, String pad, ArrayList<String> list, DirectoryTreeDocument dir) throws Exception {
        BigDecimal runningTotal = new BigDecimal(0);
        int i = 0;
        int n = 0;
        
        LeafDocument          fileNode = null;
        DirectoryTreeDocument dirNode  = null;

        try {
            if (file.isDirectory()) {

                // Get list of files in this directory.
                File dirFiles[] = file.listFiles(); 
                if (dirFiles != null) {
                	// Directory is not empty, and have access rights.

					for (i = 0; i < dirFiles.length; i++) {
						if (dirFiles[i].isDirectory()) {
							runningTotal = runningTotal.add(list(dirFiles[i],
									pad + PADSTR, list, dir));
						} else {
							runningTotal = runningTotal.add(BigDecimal
									.valueOf(dirFiles[i].length()));
							//                        list.add(pad + pad + "File: " + dirFiles[i].getCanonicalPath() + " size=" + sizeInUnits(dirFiles[i].length()));
							list.add(pad + pad + "File: "
									+ dirFiles[i].getName() + " \t"
									+ sizeInUnits(dirFiles[i].length()));
						}
					}
				}
				list.add(pad + "Dir:  " + file.getCanonicalPath() + " size=" + sizeInUnits(runningTotal.longValue()));

                dirNode = DirectoryTreeDocument.Factory.newInstance();
                dirNode.addNewDirectoryTree();
                dirNode.getDirectoryTree().setDirectoryName(file.getCanonicalPath());
                dirNode.getDirectoryTree().setDirectorySize((int)runningTotal.longValue());
//                dir.addNewDirectoryTree();
//                n = dir.getDirectoryTree().sizeOfDirectoryTreeArray();
//                dir.getDirectoryTree().setDirectoryTreeArray(n+1, dirNode.getDirectoryTree());
                dir.setDirectoryTree(dirNode.getDirectoryTree());

                return runningTotal;

            } else {
                list.add(pad + "File: " + file.getName() + " \t" + sizeInUnits(file.length()));

                fileNode = LeafDocument.Factory.newInstance();
                fileNode.addNewLeaf();
                fileNode.getLeaf().setFileName(file.getName());
                fileNode.getLeaf().setFileSize((int)file.length());
                dir.getDirectoryTree().addNewLeaf();
                n = dir.getDirectoryTree().sizeOfLeafArray();
                dir.getDirectoryTree().setLeafArray(n, fileNode.getLeaf());

                return BigDecimal.valueOf(file.length());
            }
        } catch (NullPointerException npe) {
        	System.out.println(file.getCanonicalPath());
            npe.printStackTrace();
            return BigDecimal.valueOf(0);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return BigDecimal.valueOf(0);
        }
    }


    private static String sizeInUnits(long size) {
        String ret = "";
        BigDecimal i = new BigDecimal(size);

        if (i.compareTo(GB) >= 0) {
            ret = nf.format(i.divide(GB, BigDecimal.ROUND_UP)) + "GB";

        } else if (i.compareTo(MB) >= 0) {
            ret = nf.format(i.divide(MB, BigDecimal.ROUND_UP)) + "MB";

        } else if (i.compareTo(KB) >= 0) {
            ret = nf.format(i.divide(KB, BigDecimal.ROUND_UP)) + "KB";

        } else {
            ret = nf.format(i) + " bytes";
        }
        
        return ret;
    }

    private static void addNode(XmlObject tree) {
        
    }

    private static String PADSTR = "   ";

    private final static BigDecimal KB = new BigDecimal(1024);
    private final static BigDecimal MB = new BigDecimal(1048576);
    private final static BigDecimal GB = new BigDecimal(1073732608);

    private static NumberFormat nf = NumberFormat.getNumberInstance();
} 
