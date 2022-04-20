<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Home</title>
</head>

<body>

    <h1> Welcome! </h1>


    <script src="https://unpkg.com/petite-vue" defer init></script>


    <#macro Counter initialCount>
        <div v-scope="Counter({ initialCount: ${initialCount} })">
            Initial: ${initialCount}
            Count: {{ count }}
            <button @click="increment">Increase</button>
        </div>
        <script>
            function Counter(props) {
                return {
                    count: props.initialCount,
                    increment() {
                        this.count++;
                    }
                }
            }
        </script>
        <style>
            button {
                background-color: cornflowerblue;
                border-radius: 10px;
            }
        </style>
    </#macro>


    <@Counter initialCount="1" />
    <@Counter initialCount="2" />
    <@Counter initialCount="3" />
    <@Counter initialCount="4" />


</body>

</html>