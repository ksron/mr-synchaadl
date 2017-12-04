package edu.uiuc.aadl.utils;

import org.eclipse.emf.common.util.EList;
import org.osate.aadl2.Element;
import org.osate.aadl2.modelsupport.AadlConstants;
import org.osate.aadl2.modelsupport.UnparseText;
import org.osate.aadl2.modelsupport.modeltraversal.ForAllElement;


public class RtmAadlUtil {

	private static final String NEWLINE = AadlConstants.newlineChar;

	/**
	 * Does processing of list with separators
	 *
	 * @param list
	 * @param separator
	 * @param empty			shown when the list is empty.
	 */
	static public void processEList(ForAllElement self, UnparseText aadlText,
			EList<? extends Element> list, String separator, String empty) {
		boolean first = true;
		String[] sep = separator.split(NEWLINE, -1);

		if (list == null || list.isEmpty()) {
			aadlText.addOutput(empty);
		} else {
			for (Element o : list)
			{
				if (first) {
					first = false;
				} else {
					for (int i = 0; i < sep.length; ++i) {
						if (i > 0) {
							aadlText.addOutputNewline(AadlConstants.emptyString);
						}
						aadlText.addOutput(sep[i]);
					}
				}
				self.processObject(o);
			}
		}
	}
}
