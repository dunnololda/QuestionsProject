package examination.DataLayer.dao;

import examination.DataLayer.models.Question;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Random;

@Repository
public class QuestionDAOImpl extends BaseDAOImpl implements QuestionDAO {

    private boolean addQuestion(Question question) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(question);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            log.error("Add error", e);
            return false;
        } finally {
            session.close();
        }
        return true;
    }

    private Question getQuestion(long questionID) {
        Session session = factory.openSession();
        Transaction tx = null;
        Question question = null;
        try {
            tx = session.beginTransaction();
            question = (Question) session.get(Question.class, questionID);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            log.error("Get question error: ", e);
        } finally {
            session.close();
        }
        return question;
    }

    @Override
    public Question getRandomQuestion() {
        Session session = factory.openSession();
        Transaction tx = null;
        Question question = null;
        try {
            tx = session.beginTransaction();
            Criteria criteria = session.createCriteria(Question.class);
            criteria.setProjection(Projections.rowCount());
            int count = ((Number) criteria.uniqueResult()).intValue();
            if (0 != count) {
                int index = new Random().nextInt(count);
                criteria = session.createCriteria(Question.class);
                question = (Question) criteria.setFirstResult(index)
                        .setMaxResults(1).uniqueResult();
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            log.error("Get question error: ", e);
        } finally {
            session.close();
        }
        return question;
    }

    @Override
    public List<Question> selectList(List<Long> questionIds) {
        List<Question> result;
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Criteria cr = session.createCriteria(Question.class);
            cr.add(Restrictions.in("id", questionIds));
            result = cr.list();
            tx.commit();
            return result;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            log.error("Select list error: ", e);
        }
        return null;
    }

    /* Method to UPDATE text of the question */
    private boolean updateQuestion(Question question) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(question);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            log.error("Update question error: ", e);
            return false;
        } finally {
            session.close();
        }
        return true;
    }

    /* Method to DELETE the Question from the records */
    private boolean deleteQuestion(long questionID) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Question question =
                    (Question) session.get(Question.class, questionID);
            session.delete(question);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            log.error("Delete question error: ", e);
            return false;
        } finally {
            session.close();
        }
        return true;
    }

    @Override
    public Question selectById(long id) {
        return getQuestion(id);
    }

    @Override
    public boolean insert(Question model) {
        return addQuestion(model);
    }

    @Override
    public List<Question> selectList(long offset, int limit) {
        List<Question> result;
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Criteria cr = session.createCriteria(Question.class);
            cr.setFirstResult((int) offset);      // BAD Since cast offset to int!!
            cr.setMaxResults(limit);
            result = cr.list();
            tx.commit();
            return result;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            log.error("Select list error: ", e);
        }
        return null;
    }

    //BAD BAD BAD!!! REWRITE!!!
    @Override
    public boolean deleteList(long offset, int limit) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
           /* String sql = "DELETE FROM Question WHERE id > :offset " +
                    "AND id < :maxnum";
            Query query = (Query) session.createSQLQuery(sql).addEntity(Question.class)
                    .setParameter("offset", offset).setParameter("maxnum", offset+limit);

            for (long id = offset; id < offset + limit; id++) {
                deleteQuestion(id);
            }*/
            List<Question> questions = selectList(offset, limit);
            session.delete(questions);
            tx.commit();
            return true;
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            log.error("Delete list error: ", e);
        }
        return false;
    }

    @Override
    public boolean update(Question model) {
        return updateQuestion(model);
    }

    @Override
    public boolean delete(long id) {
        return deleteQuestion(id);
    }
}


