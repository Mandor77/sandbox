package org.ascotte.codingame.mime;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Solution {

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		Map<String, String> extensions = new HashMap<String, String>();
		
		int N = in.nextInt(); // Number of elements which make up the
								// association table.
		int Q = in.nextInt(); // Number Q of file names to be analyzed.
		for (int i = 0; i < N; i++) {
			String EXT = in.next(); // file extension
			String MT = in.next(); // MIME type.
			extensions.put(EXT, MT);
		}
		in.nextLine();
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < Q; i++) {
			String FNAME = in.nextLine(); // One file name per line.
			String extension = FNAME.substring(FNAME.indexOf('.') + 1);
			if (extensions.containsKey(FNAME)) {
				buffer.append(extensions.get(FNAME));
			}
			else {
				buffer.append("UNKNOWN");
			}
		}
	}
}
