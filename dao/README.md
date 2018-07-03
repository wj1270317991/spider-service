

## 事例
```
@Mapper
public interface DemoDAO {

    @Select("SELECT * FROM demo WHERE name = #{name}")
    int findByName(@Param("name") String name);

}

```
