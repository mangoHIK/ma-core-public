/*
 * Copyright 2005 Joe Walker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.directwebremoting.servlet;

import org.directwebremoting.dwrp.PollHandler;

/**
 * A Handler polling DWR calls whose replies are NOT HTML wrapped.
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 */
public class PlainPollHandler extends PollHandler
{
    /**
     * Initialize a PollHandler to do HTML wrapping
     */
    public PlainPollHandler()
    {
        super(true);
    }
}
