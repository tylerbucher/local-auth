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
package net.reallifegames.localauth;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Permission nodes for this application.
 *
 * @author Tyler Bucher
 */
public class Permissions {

    /**
     * Permission definition class.
     */
    public static class Permission {

        /**
         * The name of this permission.
         */
        public final String name;

        /**
         * Detailed description for client side.
         */
        public final String description;

        /**
         * The numeric value for this permission.
         */
        public final int value;

        private Permission(@Nonnull final String name, @Nonnull final String description, final int value) {
            this.name = name;
            this.description = description;
            this.value = value;
        }
    }

    public static final Permission IS_USER_SUPER_ADMIN = new Permission("Super Admin", "This user is the super admin. Or owner of this application.", 0);
    public static final Permission IS_USER_ADMIN = new Permission("Admin", "This user is an admin.", 1);
    public static final Permission CAN_USER_INVITE = new Permission("Invite", "This user can invite other users.", 2);
    public static final Permission CAN_USER_MOD_INVITE = new Permission("Modify Invite", "This user can modify invites of other users.", 3);
    public static final Permission CAN_USER_DELETE_INVITE = new Permission("Delete Invite", "This user can delete invites of other users.", 4);
    public static final Permission CAN_USER_MOD_USERS = new Permission("Modify User", "This user can modify other users.", 5);
    public static final Permission CAN_USER_DELETE_USERS = new Permission("Delete Users", "This user can delete other users.", 6);
    public static final Permission CAN_USER_ADD_NODE = new Permission("Add Node", "This user can add new nodes.", 7);
    public static final Permission CAN_USER_MOD_NODE = new Permission("Add Node", "This user can modify nodes.", 8);
    public static final Permission CAN_USER_DELETE_NODE = new Permission("Add Node", "This user can delete nodes.", 9);

    /**
     * List of all assignable permission values in integer form.
     */
    public static final List<Permission> ALL_PERMISSIONS = Arrays.asList(
            IS_USER_ADMIN,
            CAN_USER_INVITE,
            CAN_USER_MOD_INVITE,
            CAN_USER_DELETE_INVITE,
            CAN_USER_MOD_USERS,
            CAN_USER_DELETE_USERS,
            CAN_USER_ADD_NODE,
            CAN_USER_MOD_NODE,
            CAN_USER_DELETE_NODE
    );

    /**
     * List of all assignable permission values in integer form.
     */
    public static final List<Integer> ALL_PERMISSION_VALUES = ALL_PERMISSIONS.stream().map(permission->permission.value).collect(Collectors.toList());

    /**
     * List of admin permissions.
     */
    public static final List<Integer> ADMIN_PERMISSIONS = Arrays.asList(
            IS_USER_ADMIN.value,
            IS_USER_SUPER_ADMIN.value
    );

    /**
     * @param permissions trims a list of permissions to a valid list.
     * @return only the valid permissions for the list.
     */
    public static List<Integer> getValidPermissions(@Nonnull final List<Integer> permissions) {
        return permissions.stream().filter(ALL_PERMISSION_VALUES::contains).collect(Collectors.toList());
    }
}
