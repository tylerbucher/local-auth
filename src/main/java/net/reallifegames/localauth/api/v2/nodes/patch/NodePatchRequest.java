/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 - Present, Tyler Bucher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.reallifegames.localauth.api.v2.nodes.patch;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.reallifegames.localauth.MongoDbModule;

import javax.annotation.Nonnull;

/**
 * Node patch request data model.
 *
 * @author Tyler Bucher
 */
public class NodePatchRequest {

    /**
     * The id for this node when sorting and applying styles.
     */
    private final String id;

    /**
     * Default text for this node if a user has not yet changed it.
     */
    private final String defaultText;

    public NodePatchRequest(@JsonProperty ("id") @Nonnull final String id,
                            @JsonProperty ("defaultText") @Nonnull final String defaultText) {
        this.id = id;
        this.defaultText = defaultText;
    }

    /**
     * @return true if the data in this request is valid false otherwise.
     */
    public boolean validate() {
        return !id.isEmpty() && !defaultText.isEmpty();
    }

    /**
     * @param dbModule the module instance to use.
     * @return true if the node was created false otherwise.
     */
    public boolean updateNode(@Nonnull final MongoDbModule dbModule) {
        return dbModule.updateNode(this.id, this.defaultText);
    }
}
