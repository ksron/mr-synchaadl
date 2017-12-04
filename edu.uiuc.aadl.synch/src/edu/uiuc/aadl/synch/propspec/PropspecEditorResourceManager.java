package edu.uiuc.aadl.synch.propspec;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.modelsupport.util.AadlUtil;

import edu.uiuc.aadl.utils.IOUtils;
import edu.uiuc.aadl.xtext.propspec.propSpec.Top;

public class PropspecEditorResourceManager {

	public enum Status {CLEARED, CHANGED, NOT_CHANGED};

	private XtextEditor editor = null;
	private Top content = null;
	private IResource modelRes = null;

	public ITextEditor getEditor() {
		return editor;
	}

	public IFile getEditorFile() {
		if (editor != null) {
			IEditorInput ipt = editor.getEditorInput();
			if  (ipt instanceof IFileEditorInput) {
				return ((IFileEditorInput)ipt).getFile();
			}
		}
		return null;
	}

	public Status setEditor(XtextEditor newEditor) {
		if (newEditor == null) {
			if (editor != null)  {
				editor = null;
				content = null;
				return Status.CLEARED;
			}
		}
		else {
			if (editor != newEditor || editor.isDirty()) {
				editor = newEditor;
				updateResource(newEditor);
				return Status.CHANGED;
			}
		}
		return Status.NOT_CHANGED;
	}

	public Top getContent() {
		return this.content;
	}

	private void setContent(Top content) {
		String path = content.getPath();
		this.modelRes = (path != null) ? IOUtils.getResource(new Path(path)) : null;
		this.content = content;
	}

	public IResource getModelResource() {
		return this.modelRes;
	}

	public IPath getCodegenFilePath() {
		IFile efile = getEditorFile();
		if (efile != null && getModelResource() != null) {
			SystemInstance si = (SystemInstance)AadlUtil.getElement(getModelResource());
			IPath context = efile.getFullPath().removeLastSegments(1);
			System.out.println(IOUtils.getCodegenPath(context, si));
			return IOUtils.getCodegenPath(context, si);
		}
		return null;
	}

	private void updateResource(XtextEditor newEditor) {
		newEditor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {
			@Override
			public void process(XtextResource resource) {
				IParseResult parseResult = resource.getParseResult();
				if(parseResult != null) {
					EObject root = parseResult.getRootASTElement();
					if(root instanceof Top) {
						setContent((Top)root);
					}
				}
			}
		});
	}

}
