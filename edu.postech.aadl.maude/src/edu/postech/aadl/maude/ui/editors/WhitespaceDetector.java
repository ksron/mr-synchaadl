/**
 * Copyright (C) The MOMENT project, 2004-2006.
 * http://moment.dsic.upv.es
 * 
 * This file is part of the Maude Simple GUI plug-in.
 * Contributed by Abel G�mez, <agomez@dsic.upv.es>.
 * 
 * The Maude Simple GUI plug-in is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * The Maude Simple GUI plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with the Maude Development Tools plugin, see the file license.txt.
 * If not, write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA  02110-1301  USA
 * 
 */
 




package edu.postech.aadl.maude.ui.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

/**
 * @author Abel G�mez. agomez@dsic.upv.es
 *
 */
 
public class WhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return Character.isWhitespace(c);
	}
}
