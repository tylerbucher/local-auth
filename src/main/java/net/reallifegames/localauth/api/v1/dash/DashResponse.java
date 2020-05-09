/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Tyler Bucher
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
package net.reallifegames.localauth.api.v1.dash;

import net.reallifegames.localauth.api.v1.ApiResponse;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A standard dash api fetch response structure.
 *
 * @author Tyler Bucher
 */
class DashResponse extends ApiResponse {

    /**
     * The list of users in this application.
     */
    public final List<String> endpoints;

    /**
     * Response constructor for Jackson json marshalling.
     *
     * @param apiResponse the root api response.
     * @param endpoints   list of endpoints in this application.
     */
    public DashResponse(@Nonnull final ApiResponse apiResponse, @Nonnull final List<String> endpoints) {
        super(apiResponse.version);
        this.endpoints = endpoints;
    }
}
