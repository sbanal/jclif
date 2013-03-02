/** 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package examples;

import java.util.ArrayList;
import java.util.List;

import org.jclif.annotation.Command;
import org.jclif.annotation.Handler;
import org.jclif.annotation.Option;
import org.jclif.annotation.Parameter;
import org.jclif.annotation.ParameterType;

@Command(identifier="list",description="List files")
public class ListCommand {
	
	@Option(identifier="s",required=true)
	private Boolean showAll;
	
	@Option(identifier="n",type=ParameterType.STRING)
	private String name;
	
	@Option(identifier="d",type=ParameterType.FILE)
	private List<String> dir = new ArrayList<String>();
	
	@Parameter(identifier="count",type=ParameterType.INTEGER,multiValued=true, required=true)
	private List<Integer> counts = new ArrayList<Integer>();
	
	@Handler
	public void execute() {
		System.out.println("List handler called. showAll=" + showAll + ",name=" + name + ",dir=" + dir + ", counts=" + counts);
	}
	
	public void setShowAll(Boolean showAll) {
		this.showAll = showAll;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDir(List<String> dir) {
		this.dir = dir;
	}
	
	public void setCounts(List<Integer> counts) {
		this.counts = counts;
	}
	
}
