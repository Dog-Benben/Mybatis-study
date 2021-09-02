import com.mybatis.dao.UserMapper;
import com.mybatis.pojo.User;
import com.mybatis.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

/**
 * @author Orange
 * @create 2021-08-19  15:59
 */
public class MyTest {

    //通过id查询用户信息
    @Test
    public void queryUserById(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        SqlSession sqlSession2 = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = mapper.queryUserById(1);
        System.out.println(user);
        sqlSession.close();//第一个会话结束后，一级缓存会存导二级缓存

//        mapper.updateUser(new User(2,"ccc","11111"));
//        sqlSession.clearCache();//手动清理缓存
        System.out.println("==================");
        UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);
        User user1 = mapper2.queryUserById(1);
        System.out.println(user1);
        System.out.println(user==user1);
        sqlSession2.close();
    }
}
