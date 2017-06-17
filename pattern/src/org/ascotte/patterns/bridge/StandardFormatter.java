package org.ascotte.patterns.bridge;

import java.util.List;

public class StandardFormatter implements Formatter {

	@Override
	public String format(String header, List<Detail> details) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(header + "\n");
		for (Detail detail:details) {
			buffer.append(detail.getLabel() + " = " + detail.getValue() + "\n");
		}
		return buffer.toString();
	}

}
