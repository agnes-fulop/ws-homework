export default function ResponseDisplay({ state }) {
  if (!state.data && !state.error) return null

  const isError = Boolean(state.error)
  return (
    <div className={`response ${isError ? 'response-error' : 'response-success'}`}>
      <div className="response-header">
        {isError ? '✗ Error' : '✓ Success'}
      </div>
      <pre>{JSON.stringify(isError ? state.error : state.data, null, 2)}</pre>
    </div>
  )
}
