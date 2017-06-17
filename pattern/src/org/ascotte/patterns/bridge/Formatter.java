package org.ascotte.patterns.bridge;

import java.util.List;

public interface Formatter {

	public String format(String header, List<Detail> details);
}
