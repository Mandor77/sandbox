package org.ascotte.patterns.bridge;

import java.util.List;

public class HtmlFormatter implements Formatter {

	@Override
	public String format(String header, List<Detail> details) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<b>" + header + "</b>" + "\n");
		for (Detail detail:details) {
			buffer.append(detail.getLabel() + " = <i>" + detail.getValue() + "</i>" + "\n");
		}
		return buffer.toString();
	}

}
