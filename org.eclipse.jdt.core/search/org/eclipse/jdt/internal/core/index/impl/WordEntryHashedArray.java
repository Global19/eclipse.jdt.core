package org.eclipse.jdt.internal.core.index.impl;

/*
 * (c) Copyright IBM Corp. 2002.
 * All Rights Reserved.
 */
import org.eclipse.jdt.internal.compiler.util.CharOperation;

public final class WordEntryHashedArray {

// to avoid using Enumerations, walk the objects skipping nulls
public WordEntry elements[];
public int elementSize; // number of elements in the table
public int threshold;

public WordEntryHashedArray() {
	this(13);
}

public WordEntryHashedArray(int size) {
	if (size < 7) size = 7;
	this.elementSize = 0;
	this.threshold = size + 1; // size is the expected number of elements
	this.elements = new WordEntry[2 * size + 1];
}

public WordEntry add(WordEntry entry) {
	int length = elements.length;
	char[] word = entry.getWord();
	int index = CharOperation.hashCode(word) % length;
	WordEntry current;
	while ((current = elements[index]) != null) {
		if (CharOperation.equals(current.getWord(), word)) return elements[index] = entry;
		if (++index == length) index = 0;
	}
	elements[index] = entry;

	// assumes the threshold is never equal to the size of the table
	if (++elementSize > threshold) grow();
	return entry;
}

public WordEntry[] asArray() {
	WordEntry[] array = new WordEntry[elementSize];
	for (int i = elements.length, j = 0; --i >= 0;) {
		WordEntry current = elements[i];
		if (current != null) array[j++] = current;
	}
	return array;
}

public WordEntry get(char[] word) {
	int length = elements.length;
	int index = CharOperation.hashCode(word) % length;
	WordEntry current;
	while ((current = elements[index]) != null) {
		if (CharOperation.equals(current.getWord(), word)) return current;
		if (++index == length) index = 0;
	}
	return null;
}

private void grow() {
	WordEntryHashedArray newArray = new WordEntryHashedArray(elementSize * 2); // double the number of expected elements
	for (int i = elements.length; --i >= 0;)
		if (elements[i] != null)
			newArray.add(elements[i]);

	this.elements = newArray.elements;
	this.elementSize = newArray.elementSize;
	this.threshold = newArray.threshold;
}

public String toString() {
	String s = ""; //$NON-NLS-1$
	WordEntry entry;
	for (int i = 0, length = elements.length; i < length; i++)
		if ((entry = elements[i]) != null)
			s += entry.toString() + "\n"; 	//$NON-NLS-1$
	return s;
}
}