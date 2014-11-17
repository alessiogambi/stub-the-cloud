/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.compute;

import java.io.IOException;
import java.util.Set;

import org.jclouds.compute.internal.BaseTemplateBuilderLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

import edu.mit.csail.sdg.squander.log.Log.Level;
import edu.mit.csail.sdg.squander.options.SquanderGlobalOptions;

@Test(groups = { "integration", "live" })
public class DeclarativeStubTemplateBuilderIntegrationTest extends BaseTemplateBuilderLiveTest {

	public DeclarativeStubTemplateBuilderIntegrationTest() {
		provider = "declarative-stub";
		SquanderGlobalOptions.INSTANCE.log_level = Level.LOG;
		SquanderGlobalOptions.INSTANCE.min_bitwidth = 10;
		SquanderGlobalOptions.INSTANCE.noOverflow = true;
	}

	@Override
	protected Set<String> getIso3166Codes() {
		return ImmutableSet.<String> of();
	}

	// @Override
	// public void testCompareSizes() throws Exception {
	// // TODO Auto-generated method stub
	// // super.testCompareSizes();
	// throw new SkipException("Problems with Preicates matchindg");
	// }

	@Override
	public void testDefaultTemplateBuilder() throws IOException {
		// TODO Auto-generated method stub
		super.testDefaultTemplateBuilder();
	}
}
