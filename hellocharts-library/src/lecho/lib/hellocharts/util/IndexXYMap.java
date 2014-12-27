/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This class is based on the achartengine IndexXYMap.java implementation.
 * https://code.google.com/p/achartengine/
 */
package lecho.lib.hellocharts.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * This class requires sorted x values
 */
public class IndexXYMap<K, V> extends TreeMap<K, V> {
  private final List<K> indexList = new ArrayList<>();

  public V put(K key, V value) {
    indexList.add(key);
    return super.put(key, value);
  }

  public V put(int index, K key, V value) {
    indexList.add(index, key);
    return super.put(key, value);
  }
  
  public V replace(int index, K key, V value) {
    removeByIndex(index);
    
    indexList.add(index, key);
    return super.put(key, value);
  }

  public void clear() {
    super.clear();
    indexList.clear();
  }

  /**
   * Returns X-value according to the given index
   * 
   * @param index
   * @return the X value
   */
  public K getXByIndex(int index) {
    return indexList.get(index);
  }

  /**
   * Returns Y-value according to the given index
   * 
   * @param index
   * @return the Y value
   */
  public V getYByIndex(int index) {
    K key = indexList.get(index);
    return this.get(key);
  }

  /**
   * Returns XY-entry according to the given index
   * 
   * @param index
   * @return the X and Y values
   */
  public Entry<K, V> getByIndex(int index) {
    K key = indexList.get(index);
    return new SimpleEntry<K, V>(key, this.get(key));
  }

  /**
   * Removes entry from map by index
   * 
   * @param index
   */
  public Entry<K, V> removeByIndex(int index) {
    K key = indexList.remove(index);
    return new SimpleEntry<K, V>(key, this.remove(key));
  }

  public int getIndexForKey(K key) {
    return Collections.binarySearch(indexList, key, null);
  }
}
