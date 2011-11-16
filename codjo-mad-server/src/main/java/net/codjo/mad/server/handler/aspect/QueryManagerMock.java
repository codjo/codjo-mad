/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler.aspect;
/**
 * Classe permettant de mocker la classe {@link QueryManager} à des fins de test.
 *
 * @version $Revision: 1.4 $
 */
public class QueryManagerMock implements QueryManager {
    private String user;
    private Query[] queries;
    private String[] handlerIds;

    /**
     * DOCUMENT ME!
     *
     * @return
     *
     * @see #mockGetHandlerIdList(String[])
     */
    public String[] getHandlerIdList() {
        return handlerIds;
    }


    /**
     * DOCUMENT ME!
     *
     * @param mockedHandlerIds
     *
     * @see #getHandlerIdList()
     */
    public void mockGetHandlerIdList(String[] mockedHandlerIds) {
        handlerIds = mockedHandlerIds;
    }


    /**
     * Il est possible de surcharger le comportement de cette méthode en implémentant la
     * méthode {link #beforeGetQuery}.
     *
     * @param handlerId
     *
     * @return
     *
     * @see #mockGetQuery(Query[])
     * @see #beforeGetQuery(String)
     */
    public Query[] getQuery(String handlerId) {
        beforeGetQuery(handlerId);

        return queries;
    }


    /**
     * Permet de compléter le comportment de la méthode getQuery.
     * 
     * <p>
     * Cette méthode est utilisé pour des tests hors librairie.
     * </p>
     *
     * @param handlerId
     */
    protected void beforeGetQuery(String handlerId) {}


    /**
     * DOCUMENT ME!
     *
     * @param mockedQueries
     *
     * @see #getQuery(String)
     */
    public void mockGetQuery(Query[] mockedQueries) {
        queries = mockedQueries;
    }


    public String getUser() {
        return user;
    }


    /**
     * Mock getUser.
     *
     * @param mockedUser
     *
     * @see #getUser()
     */
    public void mockGetUser(String mockedUser) {
        this.user = mockedUser;
    }
}
