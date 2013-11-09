package edu.uiuc.aadl.xtext.propspec.linking;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.linking.impl.DefaultLinkingService;
import org.eclipse.xtext.linking.impl.IllegalNodeException;
import org.eclipse.xtext.nodemodel.INode;
import org.osate.aadl2.Aadl2Package;
import org.osate.aadl2.CalledSubprogram;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.ComponentClassifier;
import org.osate.aadl2.ComponentPrototype;
import org.osate.aadl2.ContainedNamedElement;
import org.osate.aadl2.ContainmentPathElement;
import org.osate.aadl2.Feature;
import org.osate.aadl2.FeatureGroup;
import org.osate.aadl2.FeatureGroupPrototype;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.SubprogramCall;
import org.osate.aadl2.SubprogramImplementation;
import org.osate.aadl2.SubprogramSubcomponent;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.aadl2.modelsupport.util.ResolvePrototypeUtil;
import org.osate.aadl2.util.Aadl2Util;

import edu.uiuc.aadl.xtext.propspec.propSpec.BAExpression;
import edu.uiuc.aadl.xtext.propspec.propSpec.Prop;
import edu.uiuc.aadl.xtext.propspec.propSpec.Top;

public class PropSpecLinkingService extends DefaultLinkingService {


	/**
	 * returns the first linked object
	 */
	@Override
	public List<EObject> getLinkedObjects(EObject context, EReference reference, INode node) throws IllegalNodeException {
		final EClass requiredType = reference.getEReferenceType();
		if (requiredType == null)
			return Collections.<EObject> emptyList();
		
		final String crossRefString = getCrossRefNodeAsString(node);
		
		if (Aadl2Package.eINSTANCE.getNamedElement() == requiredType) {
			if (context instanceof ContainmentPathElement) {
				EObject res = null;
				
				if (context.eContainer() instanceof ContainedNamedElement)	// inside a path 
				{
					ContainedNamedElement path = (ContainedNamedElement) context.eContainer();
					EList<ContainmentPathElement> list = path.getContainmentPathElements();
					
					int idx = list.indexOf(context);
					if (idx > 0) 
					{
						// find next element in namespace of previous element
						ContainmentPathElement el = list.get(idx - 1);
						res = findNamedObject(el, crossRefString);
					} 
					else  // for the first containment path element 
					{ 
						ComponentClassifier ns = getContainingModelClassifier(context);
						if (ns != null)
							res = ns.findNamedElement(crossRefString);
					}
				}
				else if (context.eContainer() instanceof BAExpression)	// inside an expression
				{
					ContainmentPathElement pl = getPropPathElement(context);
					if (pl != null)
						res = findNamedObject(pl, crossRefString);
				}
				if (res != null && res instanceof NamedElement)
					return Collections.singletonList(res);
			}
		} 
		return super.getLinkedObjects(context, reference, node);
	}
	
	private static ComponentClassifier getContainingModelClassifier(EObject element) {
		EObject container = element;
		while (container != null) {
			if (container instanceof Top)
				return ((Top) container).getModel();
			container = container.eContainer();
		}
		return null;
	}
	
	private static ContainmentPathElement getPropPathElement(EObject element) {
		EObject container = element;
		while (container != null) {
			if (container instanceof Prop)
			{
				ContainedNamedElement path = ((Prop) container).getPath();
				List<ContainmentPathElement> list = path.getContainmentPathElements();
				return list.get(list.size() - 1);
			}
			container = container.eContainer();
		}
		return null;
	}

	// find an element in namespace of a given ContainmentPathElement.
	private EObject findNamedObject(ContainmentPathElement el, String crossRefString) {
		EObject res = null;
		NamedElement ne = el.getNamedElement();
		
		if (ne instanceof Subcomponent) {
			Classifier ns = ((Subcomponent) ne).getClassifier();
			if (!Aadl2Util.isNull(ns))
				res = ns.findNamedElement(crossRefString);
			// need to look for subprogram calls inside call sequences
			if (res == null) {
				if (ne instanceof ThreadSubcomponent || ne instanceof SubprogramSubcomponent) {
					if (ns instanceof ThreadImplementation) {
						res = AadlUtil.findNamedElementInList(((ThreadImplementation)ns).callSpecifications(), crossRefString);
					} 
					else if (ns instanceof SubprogramImplementation) {
						res = AadlUtil.findNamedElementInList(((SubprogramImplementation)ns).callSpecifications(), crossRefString);
					}
				}
			}
			if (res == null){
				// look in prototype actuals
				ComponentPrototype proto = ((Subcomponent) ne).getPrototype();
				ns = ResolvePrototypeUtil.resolveComponentPrototype(proto, el);
				if (ns != null)
					res = ns.findNamedElement(crossRefString);
			}
		} 
		else if (ne instanceof FeatureGroup) {
			Classifier ns = ((FeatureGroup) ne).getAllFeatureGroupType();
			if (ns != null)
				res = ns.findNamedElement(crossRefString);
			if (res == null){
				// look in prototype actuals
				FeatureGroupPrototype proto = ((FeatureGroup) ne).getFeatureGroupPrototype();
				 ns = ResolvePrototypeUtil.resolveFeatureGroupPrototype(proto, el);
				 if (ns != null)
						res = ns.findNamedElement(crossRefString);
			}
		} 
		else if (ne instanceof Feature) {
				Classifier ns = ((Feature) ne).getClassifier();
				if (ns != null)
					res = ns.findNamedElement(crossRefString);
		} 
		else if (ne instanceof SubprogramCall){
			// looking inside a subprogram that is being called
			CalledSubprogram called = ((SubprogramCall)ne).getCalledSubprogram();
			if (called instanceof SubprogramImplementation){
				res = ((SubprogramImplementation)called).findNamedElement(crossRefString);
			} else if (called instanceof SubprogramSubcomponent){
				Classifier ns = ((SubprogramSubcomponent)called).getAllClassifier();
				res = ns.findNamedElement(crossRefString);
			}
		}
		return res;
	}
}
