/*
 * Copyright (c) 2011-2014 Pivotal Software, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package reactor.bus.selector;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

/**
 * A {@link Selector} implementation based on the given regular expression. Parses it into a {@link Pattern} for
 * efficient matching against keys.
 * <p>
 * An example of creating a regex Selector would be:
 * <p>
 * <code>Selectors.R("event([0-9]+)")</code>
 * <p>
 * This would match keys like:
 * <p>
 * <code>"event1"</code>, <code>"event23"</code>, or <code>"event9"</code>
 *
 * @author Jon Brisbin
 * @author Andy Wilkinson
 */
public class RegexSelector extends ObjectSelector<Object, Pattern> {

	private final Function<Object, Map<String,Object>> headerResolver = new Function<Object, Map<String,Object>>() {
		@Nullable
		@Override
		public Map<String, Object> apply(Object key) {
			Matcher m = getObject().matcher(key.toString());
			if (!m.matches()) {
				return null;
			}
			int groups = m.groupCount();
			Map<String, Object> headers = new HashMap<String, Object>();
			for (int i = 1; i <= groups; i++) {
				String name = "group" + i;
				String value = m.group(i);
				headers.put(name, value);
			}
			return headers;
		}
	};

	/**
	 * Create a {@link Selector} when the given regex pattern.
	 *
	 * @param pattern The regex String that will be compiled into a {@link Pattern}.
	 */
	public RegexSelector(String pattern) {
		super(Pattern.compile(pattern));
	}

	/**
	 * Creates a {@link Selector} based on the given regular expression.
	 *
	 * @param regex The regular expression to compile.
	 * @return The new {@link Selector}.
	 */
	public static Selector regexSelector(String regex) {
		return new RegexSelector(regex);
	}

	@Override
	public boolean matches(Object key) {
		return key instanceof String
		  && getObject().matcher((String) key).matches();
	}

	@Override
	public Function<Object, Map<String,Object>> getHeaderResolver() {
		return headerResolver;
	}

}
