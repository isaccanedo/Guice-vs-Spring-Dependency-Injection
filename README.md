## Google Guice - Guice vs Spring – Dependency Injection

# 1. Introdução
Google Guice e Spring são duas estruturas robustas usadas para injeção de dependência. Ambos os frameworks cobrem todas as noções de injeção de dependência, mas cada um tem sua própria maneira de implementá-los.

Neste tutorial, discutiremos como os frameworks Guice e Spring diferem na configuração e implementação.

# 2. Dependências Maven
Vamos começar adicionando as dependências Guice e Spring Maven em nosso arquivo pom.xml:

```
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.1.4.RELEASE</version>
</dependency>

<dependency>
    <groupId>com.google.inject</groupId>
    <artifactId>guice</artifactId>
    <version>4.2.2</version>
</dependency>
```

Sempre podemos acessar as dependências de contexto ou guice mais recentes do Maven Central.

# 3. Configuração de injeção de dependência
A injeção de dependência é uma técnica de programação que usamos para tornar nossas classes independentes de suas dependências.

Nesta seção, vamos nos referir a vários recursos principais que diferem entre Spring e Guice em suas maneiras de configurar a injeção de dependência.

### 3.1. Spring Wiring
Spring declara as configurações de injeção de dependência em uma classe de configuração especial. Esta classe deve ser anotada pela anotação @Configuration. O contêiner Spring usa essa classe como uma fonte de definições de bean.

As classes gerenciadas pelo Spring são chamadas de Spring beans.

Spring usa a anotação @Autowired para conectar as dependências automaticamente. @Autowired faz parte das anotações centrais integradas do Spring. Podemos usar @Autowired em variáveis ​​de membro, métodos setter e construtores.

Spring também suporta @Inject. @Inject é parte do Java CDI (Contexts and Dependency Injection) que define um padrão para injeção de dependência.

Digamos que desejamos conectar automaticamente uma dependência a uma variável de membro. Podemos simplesmente anotar com @Autowired:

```
@Component
public class UserService {
    @Autowired
    private AccountService accountService;
}
```

```
@Component
public class AccountServiceImpl implements AccountService {
}
```

Em segundo lugar, vamos criar uma classe de configuração para usar como fonte de beans ao carregar nosso contexto de aplicativo:

```
@Configuration
@ComponentScan("com.isaccanedo.di.spring")
public class SpringMainConfig {
}
```

Observe que também anotamos UserService e AccountServiceImpl com @Component para registrá-los como beans. É a anotação @ComponentScan que dirá ao Spring onde procurar por componentes anotados.

Embora tenhamos anotado AccountServiceImpl, Spring pode mapeá-lo para AccountService, uma vez que implementa AccountService.

Em seguida, precisamos definir um contexto de aplicativo para acessar os beans. Vamos apenas observar que nos referiremos a este contexto em todos os nossos testes de unidade Spring:

```
ApplicationContext context = new AnnotationConfigApplicationContext(SpringMainConfig.class);
```

Agora, em tempo de execução, podemos recuperar a instância AccountService de nosso bean UserService:

```
UserService userService = context.getBean(UserService.class);
assertNotNull(userService.getAccountService());
```

### 3.2. Encadernação Guice
O Guice gerencia suas dependências em uma classe especial chamada módulo. Um módulo Guice tem que estender a classe AbstractModule e sobrescrever seu método configure().

O Guice usa a ligação como equivalente à fiação no Spring. Simplificando, as ligações nos permitem definir como as dependências serão injetadas em uma classe. As ligações do guice são declaradas no método configure() do nosso módulo.

Em vez de @Autowired, Guice usa a anotação @Inject para injetar as dependências.

Vamos criar um exemplo Guice equivalente:

```
public class GuiceUserService {
    @Inject
    private AccountService accountService;
}
```

Em segundo lugar, criaremos a classe de módulo que é uma fonte de nossas definições de ligação:

```
public class GuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AccountService.class).to(AccountServiceImpl.class);
    }
}
```

Normalmente, esperamos que o Guice instancie cada objeto de dependência de seus construtores padrão se não houver nenhuma ligação definida explicitamente no método configure(). Mas como as interfaces não podem ser instanciadas diretamente, precisamos definir ligações para dizer ao Guice qual interface será emparelhada com qual implementação.

Então, precisamos definir um injetor usando GuiceModule para obter instâncias de nossas classes. Vamos apenas observar que todos os nossos testes de Guice usarão este injetor:

```
Injector injector = Guice.createInjector(new GuiceModule());
```

Finalmente, no tempo de execução, recuperamos uma instância GuiceUserService com uma dependência accountService não nula:

```
GuiceUserService guiceUserService = injector.getInstance(GuiceUserService.class);
assertNotNull(guiceUserService.getAccountService());
```

### 3.3. Anotação @Bean de Spring
Spring também fornece uma anotação de nível de método @Bean para registrar beans como uma alternativa para suas anotações de nível de classe como @Component. O valor de retorno de um método anotado @Bean é registrado como um bean no contêiner.

Digamos que tenhamos uma instância de BookServiceImpl que desejamos disponibilizar para injeção. Poderíamos usar @Bean para registrar nossa instância:

```
@Bean 
public BookService bookServiceGenerator() {
    return new BookServiceImpl();
}
```

E agora podemos obter um bean BookService:


```
BookService bookService = context.getBean(BookService.class);
assertNotNull(bookService);
```

### 3.4. Anotação @Provides de Guice

Como um equivalente da anotação @Bean do Spring, Guice possui uma anotação @Provides embutida para fazer o mesmo trabalho. Como @Bean, @Provides só se aplica aos métodos.

Agora vamos implementar o exemplo anterior de bean Spring com Guice. Tudo o que precisamos fazer é adicionar o seguinte código em nossa classe de módulo:

```
@Provides
public BookService bookServiceGenerator() {
    return new BookServiceImpl();
}
```

E agora, podemos recuperar uma instância de BookService:

```
BookService bookService = injector.getInstance(BookService.class);
assertNotNull(bookService);
```

### 3.5. Verificação do componente Classpath na primavera
O Spring fornece uma anotação @ComponentScan que detecta e instancia componentes anotados automaticamente ao escanear pacotes predefinidos.

A anotação @ComponentScan diz ao Spring quais pacotes serão verificados em busca de componentes anotados. É usado com a anotação @Configuration.

### 3.6. Verificação do componente Classpath no Guice
Ao contrário do Spring, o Guice não possui esse recurso de varredura de componentes. Mas não é difícil simular. Existem alguns plug-ins como o Governator que podem trazer esse recurso para o Guice.

### 3.7. Reconhecimento de objetos na primavera
O Spring reconhece objetos por seus nomes. Spring mantém os objetos em uma estrutura que é mais ou menos como um Map <String, Object>. Isso significa que não podemos ter dois objetos com o mesmo nome.

A colisão de beans devido a ter vários beans com o mesmo nome é um problema comum que os desenvolvedores do Spring enfrentam. Por exemplo, vamos considerar as seguintes declarações de bean:

```
@Configuration
@Import({SpringBeansConfig.class})
@ComponentScan("com.isaccanedo.di.spring")
public class SpringMainConfig {
    @Bean
    public BookService bookServiceGenerator() {
        return new BookServiceImpl();
    }
}
```

```
@Configuration
public class SpringBeansConfig {
    @Bean
    public AudioBookService bookServiceGenerator() {
        return new AudioBookServiceImpl();
    }
}
```

Como lembramos, já tínhamos uma definição de bean para BookService na classe SpringMainConfig.

Para criar uma colisão de bean aqui, precisamos declarar os métodos de bean com o mesmo nome. Mas não podemos ter dois métodos diferentes com o mesmo nome em uma classe. Por esse motivo, declaramos o bean AudioBookService em outra classe de configuração.

Agora, vamos consultar esses beans em um teste de unidade:

```
BookService bookService = context.getBean(BookService.class);
assertNotNull(bookService); 
AudioBookService audioBookService = context.getBean(AudioBookService.class);
assertNotNull(audioBookService);
```

O teste de unidade falhará com:

```
org.springframework.beans.factory.NoSuchBeanDefinitionException:
No qualifying bean of type 'AudioBookService' available
```

Primeiro, Spring registrou o bean AudioBookService com o nome “bookServiceGenerator” em seu mapa de bean. Em seguida, ele teve que substituí-lo pela definição de bean para BookService devido à natureza "sem nomes duplicados permitidos" da estrutura de dados HashMap.

Por último, podemos superar esse problema tornando os nomes dos métodos de bean exclusivos ou definindo o atributo name como um nome exclusivo para cada @Bean.

### 3.8. Reconhecimento de objetos no Guice
Ao contrário do Spring, Guice basicamente tem uma estrutura Map <Class <?>, Object>. Isso significa que não podemos ter várias ligações para o mesmo tipo sem usar metadados adicionais.

O Guice fornece anotações de ligação para permitir a definição de várias ligações para o mesmo tipo. Vamos ver o que acontece se tivermos duas ligações diferentes para o mesmo tipo no Guice.

```
public class Person {
}
```

Agora, vamos declarar duas ligações diferentes para a classe Person:

```
bind(Person.class).toConstructor(Person.class.getConstructor());
bind(Person.class).toProvider(new Provider<Person>() {
    public Person get() {
        Person p = new Person();
        return p;
    }
});
```

E aqui está como podemos obter uma instância da classe Person:

```
Person person = injector.getInstance(Person.class);
assertNotNull(person);
```

Isso irá falhar com:

```
com.google.inject.CreationException: A binding to Person was already configured at GuiceModule.configure()
```

Podemos superar esse problema simplesmente descartando uma das ligações para a classe Person.

### 3.9. Dependências opcionais no Spring
Dependências opcionais são dependências que não são necessárias ao autowiring ou injeção de beans.

Para um campo que foi anotado com @Autowired, se um bean com o tipo de dados correspondente não for encontrado no contexto, o Spring lançará NoSuchBeanDefinitionException.

No entanto, às vezes podemos querer pular a autowiring para algumas dependências e deixá-los como nulos, sem lançar uma exceção:

Agora, vamos dar uma olhada no seguinte exemplo:

```
@Component
public class BookServiceImpl implements BookService {
    @Autowired
    private AuthorService authorService;
}
```

```
public class AuthorServiceImpl implements AuthorService {
}
```

Como podemos ver no código acima, a classe AuthorServiceImpl não foi anotada como um componente. E assumiremos que não existe um método de declaração de bean para ele em nossos arquivos de configuração.

Agora, vamos executar o seguinte teste para ver o que acontece:

```
BookService bookService = context.getBean(BookService.class);
assertNotNull(bookService);
```

Não surpreendentemente, ele irá falhar com:

```
org.springframework.beans.factory.NoSuchBeanDefinitionException: 
No qualifying bean of type 'AuthorService' available
```

Podemos tornar opcional a dependência de authorService usando o tipo opcional do Java 8 para evitar essa exceção.

```
public class BookServiceImpl implements BookService {
    @Autowired
    private Optional<AuthorService> authorService;
}
```

Agora, nossa dependência de authorService é mais como um contêiner que pode ou não conter um bean do tipo AuthorService. Mesmo que não haja um bean para AuthorService em nosso contexto de aplicativo, nosso campo authorService ainda será um contêiner vazio não nulo. Portanto, o Spring não terá nenhum motivo para lançar NoSuchBeanDefinitionException.

Como alternativa a Opcional, podemos usar o atributo obrigatório de @Autowired, que é definido como verdadeiro por padrão, para tornar uma dependência opcional. Podemos definir o atributo required como false para tornar uma dependência opcional para autowiring.

Portanto, o Spring irá pular a injeção da dependência se um bean para seu tipo de dados não estiver disponível no contexto. A dependência permanecerá definida como nula:

```
@Component
public class BookServiceImpl implements BookService {
    @Autowired(required = false)
    private AuthorService authorService;
}
```

Às vezes, marcar dependências como opcionais pode ser útil, pois nem todas as dependências são sempre necessárias.

Com isso em mente, devemos lembrar que precisaremos ter cuidado extra e verificações de nulos durante o desenvolvimento para evitar qualquer NullPointerException devido às dependências nulas.

### 3.10. Dependências opcionais no Guice
Assim como o Spring, Guice também pode usar o tipo Optional do Java 8 para tornar uma dependência opcional.

Digamos que queremos criar uma classe e com uma dependência Foo:

```
public class FooProcessor {
    @Inject
    private Foo foo;
}
```

Agora, vamos definir uma ligação para a classe Foo:


```
bind(Foo.class).toProvider(new Provider<Foo>() {
    public Foo get() {
        return null;
    }
});
```

Agora, vamos tentar obter uma instância de FooProcessor em um teste de unidade:

```
FooProcessor fooProcessor = injector.getInstance(FooProcessor.class);
assertNotNull(fooProcessor);
```

Nosso teste de unidade falhará com:

```
com.google.inject.ProvisionException:
null returned by binding at GuiceModule.configure(..)
but the 1st parameter of FooProcessor.[...] is not @Nullable
```

Para pular essa exceção, podemos tornar a dependência foo opcional com uma atualização simples:

```
public class FooProcessor {
    @Inject
    private Optional<Foo> foo;
}
```

@Inject não tem um atributo obrigatório para marcar a dependência opcional. Uma abordagem alternativa para tornar uma dependência opcional no Guice é usar a anotação @Nullable.

O Guice tolera a injeção de valores nulos no caso de usar @Nullable conforme expresso na mensagem de exceção acima. Vamos aplicar a anotação @Nullable:

```
public class FooProcessor {
    @Inject
    @Nullable
    private Foo foo;
}
```

# 4. Implementações de tipos de injeção de dependência
Nesta seção, vamos dar uma olhada nos tipos de injeção de dependência e comparar as implementações fornecidas por Spring e Guice, passando por vários exemplos.

### 4.1. Injeção de construtor na primavera
Na injeção de dependência baseada no construtor, passamos as dependências necessárias para uma classe no momento da instanciação.

Digamos que desejamos ter um componente Spring e queremos adicionar dependências por meio de seu construtor. Podemos anotar esse construtor com @Autowired:

```
@Component
public class SpringPersonService {

    private PersonDao personDao;

    @Autowired
    public SpringPersonService(PersonDao personDao) {
        this.personDao = personDao;
    }
}
```

A partir do Spring 4, a dependência @Autowired não é necessária para esse tipo de injeção se a classe tiver apenas um construtor.

Vamos recuperar um bean SpringPersonService em um teste:

```
SpringPersonService personService = context.getBean(SpringPersonService.class);
assertNotNull(personService);
```

### 4.2. Injeção de construtor no Guice
Podemos reorganizar o exemplo anterior para implementar injeção de construtor no Guice. Observe que Guice usa @Inject em vez de @Autowired.

```
public class GuicePersonService {

    private PersonDao personDao;

    @Inject
    public GuicePersonService(PersonDao personDao) {
        this.personDao = personDao;
    }
}
```

Aqui está como podemos obter uma instância da classe GuicePersonService do injetor em um teste:

```
GuicePersonService personService = injector.getInstance(GuicePersonService.class);
assertNotNull(personService);
```

### 4.3. Setter ou injeção de método na primavera
Na injeção de dependência baseada em setter, o contêiner chamará métodos setter da classe, depois de invocar o construtor para instanciar o componente.

Digamos que desejamos que o Spring autowire uma dependência usando um método setter. Podemos anotar esse método setter com @Autowired:

```
@Component
public class SpringPersonService {

    private PersonDao personDao;

    @Autowired
    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }
}
```

Sempre que precisarmos de uma instância da classe SpringPersonService, o Spring autowire o campo personDao invocando o método setPersonDao ().

Podemos obter um bean SpringPersonService e acessar seu campo personDao em um teste como abaixo:

```
SpringPersonService personService = context.getBean(SpringPersonService.class);
assertNotNull(personService);
assertNotNull(personService.getPersonDao());
```

### 4.4. Setter ou injeção de método no Guice
Simplesmente mudaremos nosso exemplo um pouco para obter injeção de setter no Guice.

```
public class GuicePersonService {

    private PersonDao personDao;

    @Inject
    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }
}
```

Sempre que obtermos uma instância da classe GuicePersonService do injetor, faremos com que o campo personDao seja passado para o método setter acima.

Aqui está como podemos criar uma instância da classe GuicePersonService e acessar seu campo personDao em um teste:

```
GuicePersonService personService = injector.getInstance(GuicePersonService.class);
assertNotNull(personService);
assertNotNull(personService.getPersonDao());
```

### 4.5. Injeção de campo na primavera
Já vimos como aplicar injeção de campo tanto para Spring quanto para Guice em todos os nossos exemplos. Portanto, não é um conceito novo para nós. Mas vamos apenas listá-lo novamente para estar completo.

No caso de injeção de dependência baseada em campo, injetamos as dependências marcando-as com @Autowired ou @Inject.

### 4.6. Injeção de campo no Guice
Como mencionamos na seção acima, já cobrimos a injeção de campo para Guice usando @Inject.

# 5. Conclusão
Neste tutorial, exploramos as várias diferenças principais entre os frameworks Guice e Spring em suas maneiras de implementar injeção de dependência. 