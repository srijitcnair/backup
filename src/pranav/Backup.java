package pranav;

import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

public class Backup {

	private static Set<String> ignoredFileNames = new HashSet<String>();
	static {
		ignoredFileNames.add("temp");
		ignoredFileNames.add("Our Downloads");
	}

	private static int[] copyFiles(File srcFolder, File targetFolder, int level) {

		boolean showDirectoryLevelreport = false;

		String levelMarker = "";
		for (int i = 0; i < level; i++) {
			levelMarker = levelMarker + "  ";
		}

		int filesCopied = 0;
		int filesSkipped = 0;
		int copyErrors = 0;

		if (!ignoredFileNames.contains(srcFolder.getName())) {
			if (srcFolder.isDirectory()
					&& (!targetFolder.exists() || (targetFolder.exists() && targetFolder.isDirectory()))) {

				if (!targetFolder.exists()) {
					if (!targetFolder.mkdir()) {
						System.err.println(levelMarker + "Error creating destination directory :"
								+ targetFolder.getAbsolutePath());
					} else {
						System.out.println(
								levelMarker + "Created destination directory :" + targetFolder.getAbsolutePath());
					}
				}

				// System.out.println(levelMarker+"------------------------------------------------");
				System.out.println(levelMarker + "Processing folder:" + srcFolder.getAbsolutePath());

				File[] files = srcFolder.listFiles();

				for (File file : files) {
					File destFile = new File(targetFolder, file.getName());

					if (file.isDirectory()) {
						int[] result = copyFiles(file, destFile, level + 1);

						filesCopied = filesCopied + result[0];
						filesSkipped = filesSkipped + result[1];
						copyErrors = copyErrors + +result[2];

					} else {

						if (!destFile.exists() || (destFile.exists() && destFile.length() != file.length())) {

							// overwrite the destination file if it exists, and
							// copy
							// the file attributes, including the rwx
							// permissions
							CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING,
									StandardCopyOption.COPY_ATTRIBUTES };
							try {
								Files.copy(file.toPath(), destFile.toPath(), options);
								filesCopied++;
							} catch (Exception e) {
								copyErrors++;
								System.err.println(levelMarker + "Error copying file from :" + file.getAbsolutePath()
										+ " to " + destFile.getAbsolutePath());
								e.printStackTrace();
							}

						} else {
							filesSkipped++;
						}

					}

				}

				if (showDirectoryLevelreport) {
					System.out.println(levelMarker + "Files copied:" + filesCopied);
					System.out.println(levelMarker + "Files skipped:" + filesSkipped);
					System.out.println(levelMarker + "Copy errors:" + copyErrors);
					// System.out.println("------------------------------------------------");
				}

			} else {
				System.err.println("Either source or destination is not a directory");
				System.err.println("Source :" + srcFolder.getAbsolutePath());
				System.err.println("Destination :" + targetFolder.getAbsolutePath());

			}
		}else{
			System.out.println(levelMarker + "Ignoring folder:" + srcFolder.getAbsolutePath());
		}
		
		return new int[] { filesCopied, filesSkipped, copyErrors };
	}

	public static void main(String[] args) {

		String fromDirectory = "C:\\Our Data";
		String toDirectory = "E:\\LenovoBackup\\Our Data";
		
		//String fromDirectory = "C:\\Our Data\\temp\\copyTest\\src";
		//String toDirectory = "C:\\Our Data\\temp\\copyTest\\dest";


		System.out.println("------------------------------------------------");

		int[] result = copyFiles(new File(fromDirectory), new File(toDirectory), 0);

		System.out.println("------------------------------------------------");
		System.out.println("Total Files copied:" + result[0]);
		System.out.println("Total Files skipped:" + result[1]);
		System.out.println("Total Copy errors:" + result[2]);
		System.out.println("------------------------------------------------");

	}

}
