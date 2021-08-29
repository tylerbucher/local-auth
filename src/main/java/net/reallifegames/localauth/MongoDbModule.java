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

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.result.UpdateResult;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperator;
import dev.morphia.query.experimental.updates.UpdateOperators;
import net.reallifegames.localauth.models.NodeModel;
import net.reallifegames.localauth.models.UserInviteModel;
import net.reallifegames.localauth.models.UserModel;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MongoDbModule {

    /**
     * A static class to hold the singleton.
     */
    private static final class SingletonHolder {

        /**
         * The Sql module singleton.
         */
        private static final MongoDbModule INSTANCE = new MongoDbModule();
    }

    /**
     * @return {@link MongoDbModule} singleton.
     */
    public static MongoDbModule getInstance() {
        return MongoDbModule.SingletonHolder.INSTANCE;
    }

    /**
     * Client for morphia to use.
     */
    private final MongoClient mongoClient;

    /**
     * morphia data object.
     */
    private final Datastore datastore;

    public MongoDbModule() {
        this(LocalAuth.getConfig().getMongoConnectionUri(), LocalAuth.getConfig().getMongoDatabaseName());
    }

    public MongoDbModule(@Nonnull final String connectionUri, @Nonnull final String dbName) {
        mongoClient = MongoClients.create(connectionUri);
        datastore = Morphia.createDatastore(mongoClient, dbName);
        datastore.getMapper().mapPackage("net.reallifegames.localauth.models");
        datastore.ensureIndexes();
    }

    /**
     * @param email id to search by.
     * @return the user model if found false otherwise.
     */
    @Nullable
    public UserModel getUserModelByEmail(@NotNull final String email) {
        return datastore.find(UserModel.class)
                .filter(Filters.eq("email", email))
                .iterator()
                .tryNext();
    }

    /**
     * @param email id to search by.
     * @return the user invite if found null otherwise.
     */
    @Nullable
    public UserInviteModel getUserInviteModelByEmail(@NotNull final String email) {
        return datastore.find(UserInviteModel.class)
                .filter(Filters.eq("email", email))
                .iterator()
                .tryNext();
    }

    /**
     * @param email    id for this user.
     * @param password password for the user.
     * @return true if the user was created false otherwise.
     */
    public boolean createNewUser(@NotNull final String email,
                                 @NotNull final String password) {
        return createNewUser(LocalAuth.getConfig(), email, password);
    }

    /**
     * @param config   module to use for account creation.
     * @param email    id for this user.
     * @param password password for the user.
     * @return true if the user was created false otherwise.
     */
    @SuppressWarnings ("ArraysAsListWithZeroOrOneArgument")
    public boolean createNewUser(@Nonnull final Config config,
                                 @NotNull final String email,
                                 @NotNull final String password) {
        final boolean isFirstUser = datastore.find(UserModel.class).count() == 0;
        final UserInviteModel userInviteModel = getUserInviteModelByEmail(email);

        final List<Integer> permList = isFirstUser ?
                Arrays.asList(Permissions.IS_USER_SUPER_ADMIN.value) : userInviteModel == null ?
                new ArrayList<>() : userInviteModel.permissions;
        if (getUserModelByEmail(email) == null) {
            if (config.getAccountCreationType() == Config.ACCOUNT_CREATION_INVITE && !isFirstUser) {
                if (userInviteModel == null) {
                    return false;
                }
            }
            datastore.save(new UserModel(
                    email,
                    BCrypt.hashpw(password, BCrypt.gensalt()),
                    isFirstUser || (userInviteModel != null),
                    true,
                    permList
            ));
            if (userInviteModel != null) {
                datastore.delete(userInviteModel);
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * @param email       email to invite to use this application.
     * @param permissions permissions this user should be given when joining.
     * @return true if invites was created false otherwise.
     */
    public boolean createNewInvite(@NotNull final String email,
                                   @NotNull final List<Integer> permissions) {
        if (email.isEmpty()) {
            return false;
        }
        datastore.save(new UserInviteModel(email, permissions));
        return true;
    }

    /**
     * @return all user invites in the system.
     */
    public List<UserInviteModel> getAllUserInviteModels() {
        return datastore.find(UserInviteModel.class)
                .filter(Filters.ne("email", ""))
                .iterator()
                .toList();
    }

    /**
     * @param email       id of the invite to update.
     * @param permissions new permissions to give the invite.
     * @return true if the invite was updated false otherwise.
     */
    public boolean updateInvite(@NotNull final String email, @Nullable final List<Integer> permissions) {
        if (permissions == null) {
            return false;
        }
        return datastore.find(UserInviteModel.class)
                .filter(Filters.eq("email", email))
                .update(UpdateOperators.set("permissions", permissions))
                .execute().getModifiedCount() == 1;
    }

    /**
     * @param email id to delete.
     * @return true if one user invite was deleted false otherwise.
     */
    public boolean deleteInvite(@Nonnull final String email) {
        return datastore.find(UserInviteModel.class)
                .filter(Filters.eq("email", email))
                .delete()
                .getDeletedCount() == 1;
    }

    /**
     * @return all users in this app.
     */
    @Nonnull
    public List<UserModel> getAllUserModels() {
        return datastore.find(UserModel.class)
                .filter(Filters.ne("email", ""))
                .iterator()
                .toList();
    }

    private static final UpdateOperator[] EMPTY_OP = new UpdateOperator[0];

    /**
     * @param email       id of the user to update
     * @param password    new password for the user, (empty string) to not update.
     * @param active      should the user be set to active.
     * @param permissions new permissions for the user. (null) to not update.
     * @return true if the user was updated false otherwise.
     */
    public boolean updateUserModel(@Nonnull final String email,
                                   @Nonnull final String password,
                                   final boolean active,
                                   @Nullable final List<Integer> permissions) {
        // We want to use updates with aggregation pipeline but morphia does not have that yet. so the solution is to use multi updates until the feature is available.
        // https://docs.mongodb.com/manual/tutorial/update-documents-with-aggregation-pipeline/
        // https://github.com/MorphiaOrg/morphia/issues/1634
        final UpdateResult result2 = datastore.find(UserModel.class)
                .filter(Filters.eq("email", email), Filters.nin("permissions", Collections.singletonList(0)))
                .update(
                        UpdateOperators.set("active", active),
                        permissions != null ? new UpdateOperator[]{UpdateOperators.set("permissions", permissions)} : new UpdateOperator[0]
                )
                .execute();
        boolean apSuccess = result2.getMatchedCount() == result2.getModifiedCount();

        if (!password.equals("")) {
            final UpdateResult result = datastore.find(UserModel.class)
                    .filter(Filters.eq("email", email))
                    .update(UpdateOperators.set("passwordHash", BCrypt.hashpw(password, BCrypt.gensalt())))
                    .execute();
            boolean pSuccess = result.getMatchedCount() == result.getModifiedCount();
            return pSuccess && (result2.getMatchedCount() == 1 || apSuccess);
        }

        return result2.getMatchedCount() != 0 && apSuccess;
    }

    /**
     * @param email id of the user to delete.
     * @return true if the user was deleted false otherwise.
     */
    public boolean deleteUser(@NotNull String email) {
        return datastore.find(UserModel.class)
                .filter(Filters.eq("email", email))
                .delete().getDeletedCount() == 1;
    }

    /**
     * @param id          id fo the new node.
     * @param defaultText default text for the node.
     * @return true if the node was created false otherwise.
     */
    public boolean createNewNode(@NotNull final String id,
                                 @NotNull final String defaultText) {
        if (id.isEmpty() || defaultText.isEmpty()) {
            return false;
        }
        datastore.save(new NodeModel(id, defaultText));
        return true;
    }

    /**
     * @param id          of the node to update.
     * @param defaultText the new text to set.
     * @return true if the node was updated false otherwise.
     */
    public boolean updateNode(@NotNull final String id,
                              @NotNull final String defaultText) {
        return datastore.find(NodeModel.class)
                .filter(Filters.eq("id", id))
                .update(UpdateOperators.set("defaultText", defaultText))
                .execute()
                .getModifiedCount() == 1;
    }

    /**
     * @param id the id to delete.
     * @return ture if the node was deleted false otherwise.
     */
    public boolean deleteNode(@Nonnull final String id) {
        return datastore.find(NodeModel.class)
                .filter(Filters.eq("id", id))
                .delete()
                .getDeletedCount() == 1;
    }

    /**
     * @return all nodes in the database.
     */
    public List<NodeModel> getAllNodeModels() {
        return datastore.find(NodeModel.class)
                .filter(Filters.ne("id", ""))
                .iterator()
                .toList();
    }

    /**
     * @param email        id to update.
     * @param nodeMetaData new data to set.
     * @return true if data was updated false otherwise.
     */
    public boolean updateUserNodeMetadata(@Nonnull final String email,
                                          @Nonnull final String nodeMetaData) {
        return datastore.find(UserModel.class)
                .filter(Filters.eq("email", email))
                .update(UpdateOperators.set("nodeMetaData", nodeMetaData))
                .execute()
                .getModifiedCount() == 1;
    }

    /**
     * Closes the mongodb connection.
     */
    public void close() {
        mongoClient.close();
    }
}
