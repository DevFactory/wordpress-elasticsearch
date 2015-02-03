/*
 *     Import wordpress data to elastic search
 *
 *     Copyright (C) 2015  Sebastian Faltoni
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.nukedbit;

import java.util.Date;

/**
 * Created by sebastian on 03/02/15.
 */
public final class Post {
    private final int id;
    private final Date modifiedAt;
    private final String content;
    private final String title;
    private final String imageUrl;

    public Post(int id, Date modifiedAt, String content, String title, String imageUrl) {

        this.id = id;
        this.modifiedAt = modifiedAt;
        this.content = content;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}